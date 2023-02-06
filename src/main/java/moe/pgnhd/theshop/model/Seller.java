package moe.pgnhd.theshop.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Seller extends User implements ResultSetConstructable {
    protected double balance;
    protected List<Product> products = new ArrayList<>();

    static Seller from(ResultSet rs) {
        try {
            return new Seller(rs);
        } catch (SQLException e) {
            return null;
        }
    }

    protected Seller(ResultSet rs) throws SQLException {
        super(rs);
        this.balance = rs.getDouble("Seller.balance");
    }

    @Override
    public String toString() {
        return firstname + " " + lastname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
