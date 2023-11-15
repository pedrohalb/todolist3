package br.edu.unifalmg.repository.book;

public class ChoreBook {

    public static final String FIND_ALL_CHORES = "SELECT * FROM db2022108043.Chore";

    public static final String INSERT_CHORE = "INSERT INTO db2022108043.Chore (`id`, `description`, `isCompleted`, `deadline`) VALUES (?,?,?,?)";

    public static final String UPDATE_CHORE = "UPDATE db2022108043.Chore SET description = ?, isCompleted = ?, deadline = ? WHERE id = ?";

}
