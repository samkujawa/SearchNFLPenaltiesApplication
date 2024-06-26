package Final;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MainApp {

    private static Connection connect = null;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/nfl_penalties";
    private static final String USER = "root";
    private static final String PASS = "Y9pq82020!";
    private static JTextField playerNameField;
    private static JTextArea resultArea;
    private static JComboBox<String> yearDropdown;
    private static JTextField playerNameForNotesField;
    private static JTextField penaltyField;
    private static JTextField yardsField;
    private static JTextField gameDateField;
    private static JTextField homeTeamField;
    private static JTextField awayTeamField;
    private static JTextArea gameResultArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("NFL Penalties Database");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        // Row 0
        JLabel playerNameLabel = new JLabel("Player Name (First initial.LastName):");
        constraints.gridx = 0;
        constraints.gridy = 0;
        frame.add(playerNameLabel, constraints);

        playerNameField = new JTextField(20);
        constraints.gridx = 1;
        frame.add(playerNameField, constraints);

        JLabel yearLabel = new JLabel("Select Year:");
        constraints.gridx = 2;
        frame.add(yearLabel, constraints);

        String[] yearOptions = { "All", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010", "2011",
                "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2021", "2022",
                "2023" };
        yearDropdown = new JComboBox<>(yearOptions);
        constraints.gridx = 3;
        frame.add(yearDropdown, constraints);

        JButton executeButton = new JButton("Search for Player Penalties");
        constraints.gridx = 4;
        frame.add(executeButton, constraints);

        // Row 1
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 5;
        resultArea = new JTextArea(10, 50);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        frame.add(scrollPane, constraints);
        constraints.gridwidth = 1;

        // Row 2
        JLabel playerNameForNotesLabel = new JLabel("Player Name:");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        frame.add(playerNameForNotesLabel, constraints);

        playerNameForNotesField = new JTextField(20);
        constraints.gridx = 1;
        frame.add(playerNameForNotesField, constraints);

        JLabel penaltyLabel = new JLabel("Penalty:");
        constraints.gridx = 2;
        frame.add(penaltyLabel, constraints);

        constraints.gridx = 3;
        penaltyField = new JTextField(20);
        frame.add(penaltyField, constraints);

        JLabel yardsLabel = new JLabel("Yards:");
        constraints.gridx = 4;
        frame.add(yardsLabel, constraints);

        constraints.gridx = 5;
        yardsField = new JTextField(20);
        frame.add(yardsField, constraints);

        JButton writeDataButton = new JButton("Write Data into 2023 Player Penalty Log");
        constraints.gridx = 6;
        constraints.gridy = 2;
        frame.add(writeDataButton, constraints);

        constraints.gridwidth = 1; // Reset to default after spanning

        // Row 3
        JLabel gameDateLabel = new JLabel("Game Date (YYYY-MM-DD):");
        constraints.gridx = 0;
        constraints.gridy = 3;
        frame.add(gameDateLabel, constraints);

        gameDateField = new JTextField(10);
        constraints.gridx = 1;
        frame.add(gameDateField, constraints);

        JLabel homeTeamLabel = new JLabel("Home Team City:");
        constraints.gridx = 2;
        frame.add(homeTeamLabel, constraints);

        constraints.gridx = 3;
        homeTeamField = new JTextField(10);
        frame.add(homeTeamField, constraints);

        JLabel awayTeamLabel = new JLabel("Away Team City:");
        constraints.gridx = 4;
        frame.add(awayTeamLabel, constraints);

        constraints.gridx = 5;
        awayTeamField = new JTextField(10);
        frame.add(awayTeamField, constraints);

        JButton searchGamesButton = new JButton("Search Games");
        constraints.gridx = 6;
        frame.add(searchGamesButton, constraints);

        // Row 4
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 5;
        gameResultArea = new JTextArea(10, 50);
        JScrollPane gameScrollPane = new JScrollPane(gameResultArea);
        frame.add(gameScrollPane, constraints);

        executeButton.addActionListener(e -> {
            String playerName = playerNameField.getText();
            executeQuery(playerName, frame);
        });

        writeDataButton.addActionListener(e -> writeData());

        searchGamesButton.addActionListener(e -> executeGameQuery(frame));

        frame.pack();
        frame.setVisible(true);
    }

    private static void writeData() {
        String playerName = playerNameForNotesField.getText();
        String penalty = penaltyField.getText();
        String yardsStr = yardsField.getText();
        String year = yearDropdown.getSelectedItem().toString();

        try {
            connectToDatabase();

            String insertLogSql = "INSERT INTO log (Penalty, Player, Yards, Date) VALUES (?, ?, ?, CURDATE())";
            PreparedStatement insertLogStatement = connect.prepareStatement(insertLogSql);
            insertLogStatement.setString(1, penalty);
            insertLogStatement.setString(2, playerName);
            insertLogStatement.setInt(3, Integer.parseInt(yardsStr));

            int logRowsInserted = insertLogStatement.executeUpdate();
            if (logRowsInserted > 0) {
                System.out.println("Data has been written to the log table.");
            } else {
                System.out.println("Failed to write data to the log table.");
            }

            String insertPlayersSql = "INSERT INTO players (Name, Penalties, Yards, Year) VALUES (?, ?, ?, ?)";
            PreparedStatement insertPlayersStatement = connect.prepareStatement(insertPlayersSql);
            insertPlayersStatement.setString(1, playerName);
            insertPlayersStatement.setString(2, penalty);
            insertPlayersStatement.setInt(3, Integer.parseInt(yardsStr));
            insertPlayersStatement.setString(4, year);

            int playersRowsInserted = insertPlayersStatement.executeUpdate();
            if (playersRowsInserted > 0) {
                System.out.println("Data has been written to the players table.");
            } else {
                System.out.println("Failed to write data to the players table.");
            }

            closeDatabaseResources(null, insertLogStatement);
            closeDatabaseResources(null, insertPlayersStatement);
        } catch (Exception ex) {
            ex.printStackTrace();
            resultArea.setText("Error occurred: " + ex.getMessage());
        }
    }

    private static void executeQuery(String playerName, JFrame frame) {
        String selectedYear = yearDropdown.getSelectedItem().toString();

        try {
            connectToDatabase();
            String sql;

            if ("All".equals(selectedYear)) {
                sql = "SELECT Name, Penalties, Yards FROM players WHERE Name LIKE ?";
            } else {
                sql = "SELECT Name, Penalties, Yards FROM players WHERE Year = ? AND Name LIKE ?";
            }

            PreparedStatement preparedStatement = connect.prepareStatement(sql);

            if (!"All".equals(selectedYear)) {
                preparedStatement.setString(1, selectedYear);
                preparedStatement.setString(2, "%" + playerName + "%");
            } else {
                preparedStatement.setString(1, "%" + playerName + "%");
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            StringBuilder resultText = new StringBuilder();
            boolean foundPenalties = false;

            while (resultSet.next()) {
                String playerNameInResult = resultSet.getString("Name");
                String penalties = resultSet.getString("Penalties");
                int penaltyYards = resultSet.getInt("Yards");

                resultText.append("Player: ").append(playerNameInResult).append(", Penalties: ").append(penalties)
                        .append(", Yards: ").append(penaltyYards).append("\n");
                foundPenalties = true;
            }

            if (foundPenalties) {
                resultArea.setText(resultText.toString());
            } else {
                resultArea.setText("No penalties found for " + playerName + " in "
                        + (selectedYear.equals("All") ? "all years" : selectedYear) + ".\n" +
                        "Keep in mind the format of names like T.Brady for names such as Tom Brady.");
            }

            closeDatabaseResources(resultSet, preparedStatement);
        } catch (Exception ex) {
            ex.printStackTrace();
            resultArea.setText("Error occurred: " + ex.getMessage());
        }
    }

    private static void connectToDatabase() throws Exception {
        if (connect == null || connect.isClosed()) {
            connect = DriverManager.getConnection(DB_URL, USER, PASS);
        }
    }

    private static void closeDatabaseResources(ResultSet resultSet, PreparedStatement preparedStatement)
            throws Exception {
        if (resultSet != null) {
            resultSet.close();
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        if (connect != null) {
            connect.close();
        }
    }

    private static void executeGameQuery(JFrame frame) {
        String gameDate = gameDateField.getText();
        String homeTeam = homeTeamField.getText();
        String awayTeam = awayTeamField.getText();

        try {
            connectToDatabase();
            StringBuilder sqlBuilder = new StringBuilder(
                    "SELECT Date, Home_Team, Away_Team, Accepted_Count FROM games WHERE 1 = 1");

            if (!gameDate.isEmpty()) {
                sqlBuilder.append(" AND Date = ?");
            }

            if (!homeTeam.isEmpty()) {
                sqlBuilder.append(" AND Home_Team LIKE ?");
            }

            if (!awayTeam.isEmpty()) {
                sqlBuilder.append(" AND Away_Team LIKE ?");
            }

            PreparedStatement preparedStatement = connect.prepareStatement(sqlBuilder.toString());

            int parameterIndex = 1;

            if (!gameDate.isEmpty()) {
                preparedStatement.setString(parameterIndex++, gameDate);
            }

            if (!homeTeam.isEmpty()) {
                preparedStatement.setString(parameterIndex++, "%" + homeTeam + "%");
            }

            if (!awayTeam.isEmpty()) {
                preparedStatement.setString(parameterIndex, "%" + awayTeam + "%");
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            StringBuilder resultText = new StringBuilder();
            boolean foundGames = false;

            while (resultSet.next()) {
                String gameDateResult = resultSet.getString("Date");
                String homeTeamResult = resultSet.getString("Home_Team");
                String awayTeamResult = resultSet.getString("Away_Team");
                int acceptedCount = resultSet.getInt("Accepted_Count");

                resultText.append("Date: ").append(gameDateResult).append(", Home Team: ").append(homeTeamResult)
                        .append(", Away Team: ").append(awayTeamResult)
                        .append(", Number of accepted penalties in that game: ").append(acceptedCount).append("\n");
                foundGames = true;
            }

            if (foundGames) {
                gameResultArea.setText(resultText.toString());
            } else {
                gameResultArea.setText("No games found for the specified criteria.");
            }

            closeDatabaseResources(resultSet, preparedStatement);
        } catch (Exception ex) {
            ex.printStackTrace();
            gameResultArea.setText("Error occurred: " + ex.getMessage());
        }
    }

}
