package biz.a7software.slimmyinvoice.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * The User class describes a user of the application, i.e. information about the user's company,
 * email address & credential information.
 */
@DatabaseTable(tableName = "owner")
public class User extends Company {

    @DatabaseField(canBeNull = false)
    private String password;
    @DatabaseField(canBeNull = false)
    private String email;
    @DatabaseField(canBeNull = false)
    private Long mustUpdate;


    public User(String vatNumber, String password, String name, Address address, String email) {
        this.vatNumber = vatNumber;
        this.password = password;
        this.name = name;
        this.address = address;
        this.email = email;
        this.mustUpdate = Long.MAX_VALUE;
    }

    public User() {
        // required for ORMLite.
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toString() {
        return this.name;
    }

    public String getEmail() {
        return email;
    }

    public Long getMustUpdate() {
        return mustUpdate;
    }

    public void setMustUpdate(Long mustUpdate) {
        this.mustUpdate = mustUpdate;
    }
}
