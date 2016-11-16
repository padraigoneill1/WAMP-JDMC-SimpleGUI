import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Assignment1 extends JFrame implements ActionListener {

	/** The name of the MySQL account to use (or empty for anonymous) */
	private final String userName = "root";

	/** The password for the MySQL account (or empty for anonymous) */
	private final String password = "";

	/** The name of the computer running MySQL */
	private final String serverName = "localhost";

	/** The port of the MySQL server (default is 3306) */
	private final int portNumber = 3306;

	/**
	 * The name of the database we are testing with
	 */
	private final String dbName = "Assignment1";

	/** The name of the table we are testing with */
	private final String tableName = "Employee";

	Statement s = null;
	ResultSet rs = null;

	private JButton nextButton;
	private JButton previousButton;
	private JButton addButton;
	private JButton deleteButton;

	private JLabel name;
	private JLabel address;
	private JLabel salary;
	private JLabel dob;
	private JLabel sex;
	private JLabel recordNumber;

	private JTextField nameField;
	private JTextField addressField;
	private JFormattedTextField salaryField;
	private JTextField sexField;
	private JFormattedTextField dobField;

	private String sql_stmt;

	public Assignment1() {
		JPanel p = new JPanel(new GridLayout(5, 3));

		name = new JLabel("Name");
		address = new JLabel("Address");
		salary = new JLabel("Salary");
		dob = new JLabel("Date Of Birth");
		sex = new JLabel("Sex");
		recordNumber = new JLabel("");

		nameField = new JTextField();
		addressField = new JTextField();
		salaryField = new JFormattedTextField();
		sexField = new JTextField();
		dobField = new JFormattedTextField();

		previousButton = new JButton("Previous");
		nextButton = new JButton("Next");
		addButton = new JButton("Add Record");
		deleteButton = new JButton("Delete");
		recordNumber = new JLabel("Sex");

		p.add(name);
		p.add(nameField);
		p.add(nextButton);
		p.add(address);
		p.add(addressField);
		p.add(previousButton);
		p.add(salary);
		p.add(salaryField);
		p.add(addButton);
		p.add(sex);
		p.add(sexField);
		p.add(deleteButton);
		p.add(dob);
		p.add(dobField);
		p.add(recordNumber);

		add(p);

		// Button action listeners
		nextButton.addActionListener(this);
		previousButton.addActionListener(this);
		addButton.addActionListener(this);
		deleteButton.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == nextButton) {
			try {
				if (rs.next() == true) { // get next entry

					nameField.setText(rs.getString("Name"));
					addressField.setText(rs.getString("Address"));
					salaryField.setValue(rs.getFloat("Salary"));
					sexField.setText(rs.getString("Sex"));
					dobField.setValue(rs.getDate("Bdate"));
					recordNumber.setText("Record No: " + rs.getRow());
				}
			} catch (SQLException e1) {
				System.out.println("Next Button Issue");
				e1.printStackTrace();
			}
		} else if (e.getSource() == previousButton) {
			try {
				if (rs.previous() == true) { // get previous entry

					nameField.setText(rs.getString("Name"));
					addressField.setText(rs.getString("Address"));
					salaryField.setValue(rs.getFloat("Salary"));
					sexField.setText(rs.getString("Sex"));
					dobField.setValue(rs.getDate("Bdate"));
					recordNumber.setText("Record No: " + rs.getRow());
				}
			} catch (SQLException e1) {
				System.out.println("Previous Button Issue");
				e1.printStackTrace();
			}
		}

		else if (e.getSource() == addButton) {
			try {
				addNew();
				run();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

		} else if (e.getSource() == deleteButton) {
			try {
				deleteRecord();
				run();
			} catch (SQLException e1) {
				e1.printStackTrace();
				infoBox("No Records to Delete", "Warning");
			}
		}
	}

	/**
	 * Add a new fixed record to table
	 * 
	 * @return
	 * @throws SQLException
	 */
	private void addNew() throws SQLException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = this.getConnection();
			statement = connection.createStatement();
			statement.executeUpdate("INSERT INTO `" + tableName
					+ "` (`Ssn`, `Bdate`, `Name`, `Address`, `Salary`, `Sex`, `Works_For`, `Manages`, `Supervises`)"
					+ " VALUES ('999', '12/12/01', 'John Green', 'Waterford', '24000.00', 'M', '1', '2', '3')");
		} catch (SQLException ex) {
			System.out.println("The following error has occured: " + ex.getMessage());
		}
	}

	/**
	 * Delete current record using SSn key
	 * 
	 * @return
	 * @throws SQLException
	 */
	private void deleteRecord() throws SQLException {
		sql_stmt = "DELETE FROM Employee WHERE Ssn = " + rs.getInt("Ssn");
		Connection connection = null;
		Statement statement = null;
		try {
			connection = this.getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(sql_stmt);
			nameField.setText(null);
			addressField.setText(null);
			salaryField.setValue(null);
			sexField.setText(null);
			dobField.setValue(null);
			recordNumber.setText("Record No: ");
		} catch (SQLException ex) {
			System.out.println("The following error has occured: " + ex.getMessage());
		}
	}

	/**
	 * Get a new database connection
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", this.userName);
		connectionProps.put("password", this.password);

		conn = DriverManager.getConnection(
				"jdbc:mysql://" + this.serverName + ":" + this.portNumber + "/" + this.dbName, connectionProps);

		return conn;
	}

	/**
	 * Connect to MySQL and get Result Set
	 */
	public void run() {

		// Connect to MySQL DB
		Connection conn = null;
		try {
			conn = this.getConnection();
			System.out.println("Connected to database");
		} catch (SQLException e) {
			System.out.println("ERROR: Could not connect to the database");
			e.printStackTrace();
			return;
		}

		try {
			System.out.println("Initialise ResultSet"); // Initialise ResultSet

			Statement s = conn.createStatement();
			s.executeQuery("SELECT * FROM Employee");
			rs = s.getResultSet();
			int count = 0;
			while (rs.next()) {
				System.out.println("Record" + count);
				String nameVal = rs.getString("Name");
				String addressVal = rs.getString("Address");
				Float salaryVal = rs.getFloat("Salary");
				String sexVal = rs.getString("Sex");
				Date dateVal = rs.getDate("Bdate");

				System.out.println("name = " + nameVal + ", AddessVal = " + addressVal + "," + " salary = " + salaryVal
						+ ", Sex = " + sexVal + ", D.O.B. = " + dateVal);
				count++;
			}

			rs.first();
			nameField.setText(rs.getString("Name"));
			addressField.setText(rs.getString("Address"));
			salaryField.setValue(rs.getFloat("Salary"));
			sexField.setText(rs.getString("Sex"));
			dobField.setValue(rs.getDate("Bdate"));
			recordNumber.setText("Record No: " + rs.getRow());
		} catch (SQLException e) {
			System.err.println("Error message: " + e.getMessage());
			System.err.println("Error number: " + e.getErrorCode());
		}
	}

	public static void infoBox(String infoMessage, String titleBar) {
		JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Connect to the DB and set up GUI & JDBC
	 */
	public static void main(String[] args) {
		Assignment1 app = new Assignment1();
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.pack();
		app.setVisible(true);
		app.run();
	}
}