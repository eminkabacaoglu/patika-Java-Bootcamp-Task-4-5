package com.gokhantamkoc.javabootcamp.odevhafta45.repository;

import com.gokhantamkoc.javabootcamp.odevhafta45.model.Product;
import com.gokhantamkoc.javabootcamp.odevhafta45.util.DatabaseConnection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@Setter
@AllArgsConstructor
public class ProductRepository {

    public static Object get;
    DatabaseConnection databaseConnection;

    @Autowired
    public void setDatabaseConnection(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }


    public List<Product> getAll() {
        final String SQL = "SELECT id, name, description FROM public.product";
        List<Product> products = new ArrayList<Product>();
        try(PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(SQL)) {
            ResultSet resSet = preparedStatement.executeQuery();
            while (resSet.next()){
                long id = resSet.getLong("id");
                String name = resSet.getString("name");
                String description = resSet.getString("description");
                products.add(new Product(id,name,description));
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    public Product get(long id) {
        final String SQL = "SELECT id, name, description FROM public.product WHERE id = ?";
        try(PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(SQL)) {
            preparedStatement.setLong(1,id);
            ResultSet resSet = preparedStatement.executeQuery();
            if(resSet.next()){
                long pid = resSet.getLong("id");
                String name = resSet.getString("name");
                String  description = resSet.getString("description");

                return new Product(pid,name,description);
            }
            else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public void save(Product product) throws RuntimeException {
        final String SQL = "INSERT INTO public.product (id,name,description) values (?,?,?)";
        try(PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(SQL)) {
            preparedStatement.setLong(1,product.getId());
            preparedStatement.setString(2,product.getName());
            preparedStatement.setString(3,product.getDescription());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows<1){
                throw new RuntimeException("Product "+product.getName()+" could not be saved !!");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());

        }
    }

    public void update(Product product) throws RuntimeException {
        Product uProduct = this.get(product.getId());
        if (uProduct != null){
            final String SQL = "UPDATE public.product SET name = ?, description = ? where id = ? ";
            try(PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(SQL)) {
                String name = product.getName();
                String description = product.getDescription();
                long id = product.getId();
                preparedStatement.setString(1,name);
                preparedStatement.setString(2,description);
                preparedStatement.setLong(3,id);
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows<1){
                    throw new RuntimeException("Product "+product.getName()+" could not be updated !!");
                }
            }catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    // BU METHODU SILMEYINIZ YOKSA TESTLER CALISMAZ
    public void delete(long id) throws RuntimeException {
        Product foundProduct = this.get(id);
        if (foundProduct != null) {
            final String SQL = "delete from public.product where id = ?";
            try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(SQL)) {
                preparedStatement.setLong(1, id);
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows <= 0) {
                    throw new RuntimeException(String.format("Could not delete product with id %d!", id));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex.getMessage());
            }
        }
    }
}
