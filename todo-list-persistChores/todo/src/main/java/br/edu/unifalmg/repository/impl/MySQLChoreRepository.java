package br.edu.unifalmg.repository.impl;

import br.edu.unifalmg.domain.Chore;
import br.edu.unifalmg.exception.*;
import br.edu.unifalmg.repository.ChoreRepository;
import br.edu.unifalmg.repository.book.ChoreBook;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MySQLChoreRepository implements ChoreRepository {
    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @Override
    public List<Chore> load() {
        if (!connectToMySQL()) {
            return new ArrayList<>();
        }

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(ChoreBook.FIND_ALL_CHORES);

            List<Chore> chores = new ArrayList<>();
            while(resultSet.next()) {
                // Poderíamos ter criado a Chore usando o construtor completo
                // OU poderíamos ter usado o construtor padrão + ter dado sets
                Chore chore = Chore.builder()
                        .description(resultSet.getString("description"))
                        .isCompleted(resultSet.getBoolean("isCompleted"))
                        .deadline(resultSet.getDate("deadline").toLocalDate())
                        .id(resultSet.getLong("id"))
                        .build();
                chores.add(chore);
            }
            return chores;
        } catch (SQLException exception) {
            System.out.println("Error when connecting to database.");
        } finally {
            closeConnections();
        }
        return null;
    }

    @Override
    public boolean saveAll(List<Chore> chores) {
        return false;
    }

    @Override
    public boolean save(Chore chore) {
        if (!connectToMySQL()) {
            return Boolean.FALSE;
        }
        try {
            preparedStatement = connection.prepareStatement(ChoreBook.INSERT_CHORE);
            preparedStatement.setLong(1, chore.getId());
            preparedStatement.setString(2, chore.getDescription());
            preparedStatement.setBoolean(3, chore.getIsCompleted());
            preparedStatement.setDate(4, Date.valueOf(chore.getDeadline()));
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        } catch (SQLException exception) {
            System.out.println("Error when inserting a new chore on database");
        } finally {
            closeConnections();
        }
        return false;
    }

    private boolean connectToMySQL() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager
                    .getConnection("jdbc:mysql://192.168.1.254:3306/db2022108043?user=user2022108043&password=2022108043");
            return Boolean.TRUE;
        } catch (ClassNotFoundException | SQLException exception) {
            System.out.println("Error when connecting to database. Try again later");
        }
        return Boolean.FALSE;
    }

    private void closeConnections() {
        try {
            if (Objects.nonNull(connection) && !connection.isClosed()) {
                connection.close();
            }
            if (Objects.nonNull(statement) && !statement.isClosed()) {
                statement.close();
            }
            if (Objects.nonNull(preparedStatement) && !preparedStatement.isClosed()) {
                preparedStatement.close();
            }
            if (Objects.nonNull(resultSet) && !resultSet.isClosed()) {
                resultSet.close();
            }
        } catch (SQLException exception) {
            System.out.println("Error when closing database connections.");
        }
    }

    @Override
    public boolean update(Chore chore){
        if(Objects.isNull(chore)){
            return false;
        }
        if(Objects.isNull(chore.getDescription()) || chore.getDescription().isEmpty()){
            throw new InvalidDescriptionException("description is null or empty");
        }
        if(Objects.isNull(chore.getDeadline()) || chore.getDeadline().isBefore(LocalDate.now())){
            throw new InvalidDeadlineException("deadLine is null or before now");
        }
        if(Objects.isNull(chore.getIsCompleted())){
            return false;
        }
        if(Objects.isNull(chore.getId())){
            return false;
        }
        if (!connectToMySQL()) {
            return false;
        }
        try {
            preparedStatement = connection.prepareStatement(ChoreBook.UPDATE_CHORE);

            preparedStatement.setLong(4, chore.getId());
            preparedStatement.setString(1, chore.getDescription());
            preparedStatement.setBoolean(2, chore.getIsCompleted());
            preparedStatement.setDate(3, Date.valueOf(chore.getDeadline()));
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        } catch (SQLException exception) {
            System.out.println("Error when connecting to database.");
        } finally {
            closeConnections();
        }
        return false;
    }

}
