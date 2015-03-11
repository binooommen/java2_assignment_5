/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assign4;

import com.mysql.jdbc.Connection;
import java.io.StringReader;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.stream.JsonParser;
//import javax.persistence.*;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Size;
import javax.ws.rs.*;
import org.json.simple.JSONArray;

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
        String result = new String();
        try (Connection conn = getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();
            JSONArray productArr = new JSONArray();
            while (rs.next()) {
                Map productMap = new LinkedHashMap();
                productMap.put("productID", rs.getInt("productID"));
                productMap.put("name", rs.getString("name"));
                productMap.put("description", rs.getString("description"));
                productMap.put("quantity", rs.getInt("quantity"));
                productArr.add(productMap);
            }
            result = productArr.toString();
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result.replace("},", "},\n");
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
//    @GET
//    @Produces("application/json")
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
//        try (PrintWriter out = response.getWriter()) {
//            if (!request.getParameterNames().hasMoreElements()) {
//                out.println(getResults("SELECT * FROM product"));
//            } else {
//                int productID = Integer.parseInt(request.getParameter("productID"));
//                out.println(getResults("SELECT * FROM product WHERE productID = ?", String.valueOf(productID)));
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
//    @POST
//    @Consumes("application/json")
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        Set<String> keySet = request.getParameterMap().keySet();
//        try (PrintWriter out = response.getWriter()) {
//            Connection conn = getConnection();
//            if (keySet.contains("name") && keySet.contains("description") && keySet.contains("quantity")) {
//                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `product`(`productID`, `name`, `description`, `quantity`) "
//                        + "VALUES (null, '" + request.getParameter("name") + "', '"
//                        + request.getParameter("description") + "', "
//                        + request.getParameter("quantity") + ");"
//                );
//                try {
//                    pstmt.executeUpdate();
//                    request.getParameter("productID");
//                    doGet(request, response);
//                } catch (SQLException ex) {
//                    Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
//                    out.println("Data inserted Error while retriving data.");
//                }
//            } else {
//                out.println("Error: Not enough data to input");
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    @Override
//    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        Set<String> keySet = request.getParameterMap().keySet();
//        try (PrintWriter out = response.getWriter()) {
//            Connection conn = getConnection();
//            if (keySet.contains("productID") && keySet.contains("name") && keySet.contains("description") && keySet.contains("quantity")) {
//                PreparedStatement pstmt = conn.prepareStatement("UPDATE `product` SET `name`='"
//                        + request.getParameter("name") + "',`description`='"
//                        + request.getParameter("description")
//                        + "',`quantity`=" + request.getParameter("quantity")
//                        + " WHERE `productID`=" + request.getParameter("productID"));
//                try {
//                    pstmt.executeUpdate();
//                    doGet(request, response); //shows updated row
//                } catch (SQLException ex) {
//                    Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
//                    out.println("Error putting values.");
//                }
//            } else {
//                out.println("Error: Not enough data to update");
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    @Override
//    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        Set<String> keySet = request.getParameterMap().keySet();
//        try (PrintWriter out = response.getWriter()) {
//            Connection conn = getConnection();
//            if (keySet.contains("productID")) {
//                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM `product` WHERE `productID`=" + request.getParameter("productID"));
//                try {
//                    pstmt.executeUpdate();
//                } catch (SQLException ex) {
//                    Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
//                    out.println("Error in deleting the product.");
//                }
//            } else {
//                out.println("Error: in data to delete");
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
}
