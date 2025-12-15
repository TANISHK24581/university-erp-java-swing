package edu.univ.erp.auth;

import edu.univ.erp.data.AuthDB;
import edu.univ.erp.domain.User;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    private static AuthService instance;

    private int userId;
    private String role;
    private String username;
    private int attemptCount = 0;
    private long lockUntil = 0;


    private AuthService() { }

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    /** ------------------------- LOGIN ------------------------- */
    public boolean login(String username, String password) {

        long now = System.currentTimeMillis();

        // --- If temporarily locked, do not allow login ---
        if (lockUntil > now) {
            return false;
        }

        // --- existing logic starts ---
        User user = AuthDB.getUserByUsername(username);
        if (user == null) {
            attemptCount++;
            checkLock(now);
            return false;
        }

        String hash = AuthDB.getPasswordHash(username);
        if (hash == null) {
            attemptCount++;
            checkLock(now);
            return false;
        }

        boolean match = BCrypt.checkpw(password, hash);
        if (!match) {
            attemptCount++;
            checkLock(now);
            return false;
        }

        // --- Login successful ---
        attemptCount = 0;
        lockUntil = 0;

        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.role = user.getRole();

        AuthDB.updateLastLogin(user.getUserId());

        return true;
    }
    private void checkLock(long now) {
        if (attemptCount >= 5) {
            lockUntil = now + 60_000;  // lock for 60 seconds
        }
    }
    public boolean isLocked() {
        return System.currentTimeMillis() < lockUntil;
    }

    public int getRemainingSeconds() {
        long diff = lockUntil - System.currentTimeMillis();
        return (int)Math.max(0, diff / 1000);
    }

    public int getAttemptCount() {
        return attemptCount;
    }



    /** ------------------------- LOGOUT ------------------------- */
    public void logout() {
        this.userId = 0;
        this.username = null;
        this.role = null;
    }

    /** Returns true if someone is logged in */
    public boolean isLoggedIn() {
        return userId != 0;
    }

    /** --------------------- CHANGE PASSWORD --------------------- */
    public String changePassword(String oldPass, String newPass) {
        if (!isLoggedIn()) {
            return "Not logged in.";
        }

        String currentHash = AuthDB.getPasswordHash(username);
        if (currentHash == null) {
            return "User not found.";
        }

        // Verify old password
        if (!BCrypt.checkpw(oldPass, currentHash)) {
            return "Old password incorrect.";
        }

        // Hash new password
        String newHash = BCrypt.hashpw(newPass, BCrypt.gensalt());

        boolean ok = AuthDB.updatePasswordHash(userId, newHash);
        return ok ? "Password changed successfully." : "Failed to update password.";
    }

    /** ---------------------- GETTERS --------------------------- */
    public int getUserId() { return userId; }
    public String getRole() { return role; }
    public String getUsername() { return username; }
}
