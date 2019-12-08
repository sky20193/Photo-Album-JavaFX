package controller;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.File;
import java.io.FileInputStream;
import model.*;

/**
 * Controller for the login view of the application. Goes to admin view if 
 * admin logs in, otherwise goes to user view.
 * 
 * @author Umang Patel
 * @author Akashkumar Patel
 */
public class LoginController {
	
	@FXML // fx:id="TFLoginUsername"
    private TextField TFLoginUsername; // Value injected by FXMLLoader
        
    List<String> usernames;
    
    /**
     * Initializes login controller by loading list of names of existing users.
     * 
     * @throws MalformedURLException
     */
    @SuppressWarnings("unchecked")
	public void init() throws MalformedURLException {
    	try {
    		FileInputStream fs = new FileInputStream("data/usernames.ser");
    		ObjectInputStream os = new ObjectInputStream(fs);
    		
    		usernames = (ArrayList<String>)os.readObject();
    		
    		os.close();
    		fs.close();
    	}
    	catch(FileNotFoundException e) {
    		e.printStackTrace();
    		//file doesn't exist, no users other than admin
    		usernames = new ArrayList<String>();
    	}
    	catch(IOException e) {
    		e.printStackTrace();
    	}
    	catch(ClassNotFoundException e) {
    		e.printStackTrace();
    	}
    	
    	if(!usernames.contains("stock")) {
    		User stockuser = new User("stock");
    		Album stockalbum = new Album("stock");
    		
    		ArrayList<String> stockfiles = new ArrayList<String>();
    		stockfiles.add("./data/fire.png");
    		stockfiles.add("./data/garagedoor.jpg");
    		stockfiles.add("./data/grass.jpg");
    		stockfiles.add("./data/leaves.jpg");
    		stockfiles.add("./data/cactus.jpg");
    		
    		for(String filename: stockfiles) {
	    		File f = new File(filename);
	    		if(!f.exists())
	    			return;
	    		Photo p = new Photo(f.toURI().toURL().toExternalForm());
	        	Calendar cal = Calendar.getInstance();
	        	cal.setTimeInMillis(f.lastModified());
	        	cal.set(Calendar.MILLISECOND, 0);
	        	p.setDateTime(cal);
	        	stockalbum.addPhoto(p);
	    	}
        	stockuser.addAlbum(stockalbum);
        	
        	
    		try {
				FileOutputStream fs = new FileOutputStream("data/"+stockuser.getName()+".ser");
				ObjectOutputStream os = new ObjectOutputStream(fs);
				os.writeObject(stockuser);
				os.close();
				fs.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
    		
    		usernames.add("stock");
    	}
    }

    @FXML
    private void onClickExit(ActionEvent event) {
    	System.exit(0); 
    }
    
	@FXML
    private void onClickLogin(ActionEvent event) throws Exception {
    	String username = TFLoginUsername.getText();
    	
    	if(username.equals("admin")) {
    		
    		((Node)event.getSource()).getScene().getWindow().hide();
    		
    		Stage primaryStage = new Stage();
    		FXMLLoader loader = new FXMLLoader();
    		loader.setLocation(getClass().getResource("/view/Adminfxml.fxml"));
    		AnchorPane root = (AnchorPane)loader.load();
    		AdminController con = (AdminController)loader.getController();
    		con.init(usernames, primaryStage);
    		Scene scene = new Scene(root);
    		primaryStage.setScene(scene);
    		primaryStage.initStyle(StageStyle.DECORATED);
    		primaryStage.setTitle("Admin Page");
    		primaryStage.setResizable(false);
    		primaryStage.show();
    		return;
    	}
    	
    	if(!usernames.contains(username)) {
    		Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText("INVALID USERNAME");
            alert.setContentText(username + " does not exist.");
     
            alert.showAndWait();
            TFLoginUsername.clear();
            return;
    	}
    	
    	((Node)event.getSource()).getScene().getWindow().hide();
		
		Stage userStage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/UserAlbum.fxml"));
		AnchorPane root = (AnchorPane)loader.load();
		UserAlbumController con = (UserAlbumController)loader.getController();
		con.init(username, userStage);
		Scene scene = new Scene(root);
		userStage.setScene(scene);
		userStage.initStyle(StageStyle.DECORATED);
		userStage.setTitle("User Page");
		userStage.setResizable(false);    		
		userStage.show();
    }
}