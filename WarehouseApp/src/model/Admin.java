package model;

public class Admin {
    private int id;
    private String username;
    private String password;
    private String role; // Kolom baru untuk peran

    // Konstruktor diperbarui
    public Admin(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password; // Sebaiknya password di-hash, tapi untuk contoh ini kita biarkan
        this.role = role;
    }
    
    // Konstruktor untuk admin baru (tanpa ID)
    public Admin(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }


    // -- Kumpulan Getter dan Setter --

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Metode praktis untuk memeriksa apakah pengguna adalah super admin.
     * @return true jika role adalah 'super_admin'.
     */
    public boolean isSuperAdmin() {
        return "super_admin".equalsIgnoreCase(this.role);
    }
}