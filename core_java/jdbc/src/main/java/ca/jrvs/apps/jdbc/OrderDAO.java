package ca.jrvs.apps.jdbc;

import ca.jrvs.apps.jdbc.util.DataAccessObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OrderDAO extends DataAccessObject<Order> {

    private static final String READ = "SELECT\n" +
            "  c.first_name cust_first_name, c.last_name cust_last_name, c.email cust_email, o.order_id,\n" +
            "  o.creation_date, o.total_due, o.status,\n" +
            "  s.first_name sales_first_name, s.last_name sales_last_name, s.email sales_email,\n" +
            "  ol.quantity,\n" +
            "  p.code, p.name, p.size, p.variety, p.price\n" +
            "from orders o\n" +
            "  join customer c on o.customer_id = c.customer_id\n" +
            "  join salesperson s on o.salesperson_id=s.salesperson_id\n" +
            "  join order_item ol on ol.order_id=o.order_id\n" +
            "  join product p on ol.product_id = p.product_id\n" +
            "where o.order_id = ?;";

    public OrderDAO(Connection connection) {
        super(connection);
    }

    @Override
    public Order findById(long id) {
        Order order = new Order();
        try (PreparedStatement statement = this.connection.prepareStatement(READ);) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            int counter = 0;
            while (rs.next()) {
                if (counter == 0){
                    order.setId(rs.getLong("order_id"));
                    order.setCustFirstName(rs.getString("cust_first_name"));
                    order.setCustLastName(rs.getString("cust_last_name"));
                    order.setCustEmail(rs.getString("cust_email"));
                    order.setCreationDate(rs.getString("creation_date"));
                    order.setTotalDue(rs.getDouble("total_due"));
                    order.setStatus(rs.getString("status"));
                    order.setSalesFirstName(rs.getString("sales_first_name"));
                    order.setSalesLastName(rs.getString("sales_last_name"));
                    order.setSalesEmail(rs.getString("sales_email"));
                    counter = 1;
                }
                OrderLine orderItem = new OrderLine();
                orderItem.setQuantity(rs.getInt("quantity"));
                orderItem.setProductCode(rs.getString("code"));
                orderItem.setProductName(rs.getString("name"));
                orderItem.setProductSize(rs.getString("size"));
                orderItem.setProductVariety(rs.getString("variety"));
                orderItem.setProductPrice(rs.getDouble("price"));
                order.addOrderLine(orderItem);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return order;
    }

    public List<Order> findAll() {
        return null;
    }
    public Order update(Order dto) {
        return null;
    }
    public Order create(Order dto) {
        return null;
    }
    public void delete(long id) {
    }
}
