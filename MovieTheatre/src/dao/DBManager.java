package dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import core.Employee;
import core.Movie;

public class DBManager {
	
	private Connection con;
	
	public DBManager() {
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e){
			System.out.println("Where is your Oracle JDBC Driver?");
			e.printStackTrace();
			return;
		}
		
		//connect to database
		try {
			con = DriverManager.getConnection(
					  "jdbc:oracle:thin:@localhost:1522:ug", "ora_n2v8", "a36847127");
		} catch (SQLException e) {
			System.out.println("Connection failed");
			e.printStackTrace();
			return;
		}
		
		if (con != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
		
		try {
			con.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

////// Employee	
	public String[] getEIDEname(String eid) throws SQLException{
		String[] returnString = new String[2];
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			stmt = con.prepareStatement("Select eid,ename from employee where eid like ?");
			stmt.setString(1, eid);
			rs = stmt.executeQuery();
			
			while (rs.next()){
				returnString[0] = rs.getString("eid");
				System.out.println(returnString[0]);
				returnString[1] = rs.getString("ename");
				System.out.println(returnString[1]);
			}
			
		}finally{
			close(stmt,rs);
		}
		return returnString;
	}
	
	public Boolean checkManager(String eid) throws SQLException{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Boolean result = false;
		
		try {
			stmt = con.prepareStatement("select count(*) from manager where eid like ?");
			stmt.setString(1, eid);
			rs = stmt.executeQuery();
			while(rs.next()){
				result = rs.getBoolean(1);
				System.out.println(result);
			}
		} finally{
			close(stmt,rs);
		}

		
		return result;
	}
	
	
	
	public String[] managerLogin(String eid) throws SQLException{
		if(checkManager(eid)){
			String[] eidEnamePair =getEIDEname(eid);
			return eidEnamePair;
		} else {
			throw new SQLException();
		}
		
	}
	
	public void deleteEmployee(String eid) throws SQLException{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			stmt = con.prepareStatement("delete from employee where eid like ?");
			stmt.setString(1, eid);
			rs = stmt.executeQuery();
		}finally{
			close(stmt,rs);
		}
	}
	
	public List<Employee> getAllEmployee() throws SQLException{
		List<Employee> employeeList = new ArrayList<Employee>();
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			stmt = con.createStatement();
			rs = stmt.executeQuery("select * from employee");
			while(rs.next()){
				Employee employee = convertRowToEmployee(rs);
				employeeList.add(employee);
			}
			
			return employeeList;
		} finally{
			close(stmt,rs);
		}
	}
	
	public Employee convertRowToEmployee(ResultSet rs) throws SQLException{

		String eid = rs.getString("eid");
		String name = rs.getString("ename");
		String phone = rs.getString("ephone");
		Integer salary = rs.getInt("salary");
		Employee employee = new Employee(eid,name,phone,salary);
		
		return employee;
	}

	
//////Movie
	public List<Movie> getAllMovie() throws Exception{
		
			List<Movie> movieList = new ArrayList<Movie>();
			Statement stmt = null;
			ResultSet rs = null;
			
			try {
				stmt = con.createStatement();
				rs = stmt.executeQuery("select * from movie natural left join performed_by natural left join directed_by");
			while(rs.next()){
				Movie tempMovie = convertRowToMovie(rs);
				if(movieList.contains(tempMovie)){
					int index = movieList.indexOf(tempMovie);
					Movie sameMovie = movieList.get(index);
					tempMovie.mergeDirector(sameMovie);
					tempMovie.mergeActor(sameMovie);
					movieList.remove(index);
				}
				movieList.add(tempMovie);
			}
			
			return movieList;
		}
		finally{
			close(stmt,rs);
		}
	}

	public List<Movie> displayByGenre(String genre) throws Exception{
		List<Movie> movieList = new ArrayList<Movie>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			stmt = con.prepareStatement("SELECT * FROM movie natural join performed_by, directed_by WHERE genre like ?");
			stmt.setString(1, genre);
			rs = stmt.executeQuery();
			
			while(rs.next()){
				Movie tempMovie = convertRowToMovie(rs);
				if(movieList.contains(tempMovie)){

					int index = movieList.indexOf(tempMovie);
					Movie sameMovie = movieList.get(index);
					tempMovie.mergeDirector(sameMovie);
					tempMovie.mergeActor(sameMovie);
					movieList.remove(index);
				}
				
				movieList.add(tempMovie);
			}
			
			return movieList;
		}
		finally{
			close(stmt,rs);
		}
	}
	
	
	private Movie convertRowToMovie(ResultSet rs) throws SQLException{

		String title = rs.getString("title");
		String genre = rs.getString("genre");
		String language = rs.getString("language");
		int length = rs.getInt("length");
		String movieID = rs.getString("movie_ID");
		String rating = rs.getString("rating");
		Set<String> directorList = new HashSet<String>();
		directorList.add(rs.getString("dname"));
		Set<String> actorList = new HashSet<String>();
		actorList.add(rs.getString("aname"));
		
		Movie tempMovie = new Movie(title, genre, language, length, movieID, rating, directorList, actorList);
		
		return tempMovie;
	}
	
	private static void close(Connection con, Statement stmt, ResultSet rs)
			throws SQLException {

		if (rs != null) {
			rs.close();
		}

		if (stmt != null) {
			
		}
		
		if (con != null) {
			con.close();
		}
	}
	
	public static void printMovies(List<Movie> movies){
		for (Movie m:movies){
			System.out.println(m.printMovie());
		}
	}
	
//////Ticket
	public List<String> seeMostSoldMovie() throws Exception{
		// sql query find the max:
		// get all tickets group by movieID
		// compute avg for each group: sumofisSold/countOfTicket
		//return the movieID of the most sold movie
		List<String> movieList = new ArrayList<String>();
		
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("max");
		while(rs.next()){
			String movieID = rs.getString("movie_ID");
			movieList.add(movieID);
		}
		
		return movieList;
		}
		finally{
			close(stmt,rs);
		}
	}
	
	public List<String> seeLeastSoldMovie() throws Exception{
		//return the movieID of the least sold movie
		List<String> movieList = new ArrayList<String>();
		
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("max");
		while(rs.next()){
			String movieID = rs.getString("movie_ID");
			movieList.add(movieID);
		}
		
		return movieList;
		}
		finally{
			close(stmt,rs);
		}
	}
	
	public static void printLeastOrMostSoldMovie(List<String> movies){
		for (String m:movies){
			printMovieID(m);
		}
	}
	
	public static void printMovieID(String m){
		System.out.println(m);
	}
	

	public void quit(){
	    try {
            con.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

	private void close(Statement stmt, ResultSet rs) throws SQLException {
		close(null, stmt, rs);		
	}
	
	public static void main(String[] args) throws Exception{

		DBManager dao = new DBManager();

		//MovieDAO.printMovies(dao.displayByGenre("family"));
		DBManager.printMovies(dao.getAllMovie());	
		
	}

	

	

}