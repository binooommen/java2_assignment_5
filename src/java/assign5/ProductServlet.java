/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assign5;

import com.mysql.jdbc.Connection;
import java.io.StringReader;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.stream.JsonParser;
import javax.ws.rs.*;

/**
 *
 * @author c0647015
 */
@Path("/products")
public class ProductServlet {

    @GET
    @Produces("application/json")
    public String doGet() {
        return getResults("SELECT * FROM product");
    }

    @GET
    @Produces("application/json")
    @Path("{id}")
    public String doGet(@PathParam("id") String productID) {
        return getResults("SELECT * FROM product WHERE productID = ?", productID);
    }

    @POST
    @Consumes("application/json")
    public void doPost(String str) {
        JsonParser parser = Json.createParser(new StringReader(str));
        Map<String, String> mapKeyValue = new HashMap<>();
        String key = "", val;
        while (parser.hasNext()) {
            JsonParser.Event evt = parser.next();
            switch (evt) {
                case KEY_NAME:
                    key = parser.getString();
                    break;
                case VALUE_STRING:
                    val = parser.getString();
                    mapKeyValue.put(key, val);
                    break;
                case VALUE_NUMBER:
                    val = Integer.toString(parser.getInt());
                    mapKeyValue.put(key, val);
                    break;
            }
        }
        System.out.println(mapKeyValue);
        doPostOrPutOrDelete("INSERT INTO product (name, description, quantity) VALUES ( ?, ?, ?)",
                mapKeyValue.get("name"), mapKeyValue.get("description"), mapKeyValue.get("quantity"));
    }

    @PUT
    @Path("{id}")
    @Consumes("application/json")
    public void doPut(@PathParam("id") String id, String str) {
        JsonParser parser = Json.createParser(new StringReader(str));
        Map<String, String> mapKayValue = new HashMap<>();
        String key = "", val;
        while (parser.hasNext()) {
            JsonParser.Event evt = parser.next();
            switch (evt) {
                case KEY_NAME:
                    key = parser.getString();
                    break;
                case VALUE_STRING:
                    val = parser.getString();
                    mapKayValue.put(key, val);
                    break;
                case VALUE_NUMBER:
                    val = parser.getString();
                    mapKayValue.put(key, val);
                    break;
            }
        }
        System.out.println(mapKayValue);
        doPostOrPutOrDelete("UPDATE PRODUCT SET name = ?, description = ?, quantity = ? WHERE productID = ?",
                mapKayValue.get("name"), mapKayValue.get("description"), mapKayValue.get("quantity"), id);

    }

    @DELETE
    @Path("{id}")
    public void doDelete(@PathParam("id") String id, String str) {
        doPostOrPutOrDelete("DELETE FROM product WHERE productID = ?", id);
    }

    private void doPostOrPutOrDelete(String query, String... params) {
        try (Connection conn = getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        getResults("SELECT * FROM product");
    }

    private Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String jdbc = "jdbc:mysql://localhost/fastenlt";
            conn = (Connection) DriverManager.getConnection(jdbc, "root", "");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

    private String getResults(String query, String... params) {
        JsonArrayBuilder productArr = Json.createArrayBuilder();
        String res = new String();
        try (Connection conn = getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                JsonObjectBuilder json = Json.createObjectBuilder()
                        .add("productID", rs.getInt("productID"))
                        .add("name", rs.getString("name"))
                        .add("description", rs.getString("description"))
                        .add("quantity", rs.getInt("quantity"));
                res = json.build().toString();
                productArr.add(json);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (params.length == 0) {
            res = productArr.build().toString();
        }
        return res;
    }
}
