package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import model.User;

/**
 * Controller class for admin view, where the admin can create/delete users.
 * An empty User instance is created when a user is added, and a file to store that user's 
 * data is created.
 * 
 * @author Umang Patel
 * @author Akashkumar Patel
 */
public class AdminController {
    @FXML // fx:id="LVAdminUserList"
    private ListView<String> LVAdminUserList; // Value injected by FXMLLoader

    ObservableList<String> userObsList;   
    List<String> users;
    
    /**
     * Initializes controller using a list of usernames.
     */
    public void init(List<String> users, Stage stage) {
    	this.users = users;
    	userObsList = FXCollections.observableList(users);
 	    LVAdminUserList.setItems(userObsList);
 	    FXCollections.sort(userObsList);
 	   
 	    if(!userObsList.isEmpty())
 		    LVAdminUserList.getSelectionModel().select(0);
 	    
 	    stage.setOnCloseRequest(e -> this.saveUserList());
    }

    @FXML
    private void onClickAddUser(ActionEvent event) throws IOException {
    	
    	TextInputDialog text = new TextInputDialog();
    	text.setTitle("Add user");
    	text.setHeaderText("Enter the user name to be added.");
    	Optional<String> result = text.showAndWait();
    	
    	
    	if(result.isPresent() && !result.get().equals("")) {
    		
    		//check to make sure that admin is not entered as the username.
    		if(result.get().equalsIgnoreCase("admin")) {
    			Alert alert = new Alert(AlertType.ERROR);
    			alert.setTitle("Alert!!");
    			alert.setHeaderText("CANNOT ADD 'admin' OR 'ADMIN' as username");
    			alert.showAndWait();
    			return;
    		}
    		
    		//check to make sure user doesn't already exist
    		for(String user: userObsList) {
    			if(user.equalsIgnoreCase(result.get())) {
    				Alert err = new Alert(Alert.AlertType.WARNING, "Username already exists.", ButtonType.OK);
    				err.showAndWait();
    				return;
    			}
    		}
    		
			User u = new User(result.get());
			userObsList.add(u.getName());
			FXCollections.sort(userObsList);
			LVAdminUserList.getSelectionModel().select(u.getName());
			
			try {
				FileOutputStream fs = new FileOutputStream("data/"+u.getName()+".ser");
				ObjectOutputStream os = new ObjectOutputStream(fs);
				os.writeObject(u);
				os.close();
				fs.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
    	}
    }
    
     
    @FXML
    private void onClickDeleteUser(ActionEvent event) throws Exception {
    	if(userObsList.isEmpty()) {
        	Alert err = new Alert(Alert.AlertType.ERROR, "No User to delete", ButtonType.OK);
            err.showAndWait();
            return;
    	}
    	
    	String user = LVAdminUserList.getSelectionModel().getSelectedItem();
    	
		//confirm deletion
    	Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + user +" ?", ButtonType.YES, ButtonType.CANCEL);
        confirm.showAndWait();
        if (confirm.getResult() != ButtonType.YES)
            return;
        
        //determine which user to select after deletion
        int count = userObsList.size();
        int index = LVAdminUserList.getSelectionModel().getSelectedIndex();
        
        if(count-1 == index)
        	LVAdminUserList.getSelectionModel().selectPrevious();
        else
        	LVAdminUserList.getSelectionModel().selectNext();
        
        //delete user from list
        userObsList.remove(user);
        
        //delete file of that user
        String filename = user + ".ser";
        File f = new File("data/"+filename);
        f.delete();
    }
    
    private void saveUserList() {
    	try {
    		FileOutputStream fs = new FileOutputStream("data/usernames.ser");
			ObjectOutputStream os = new ObjectOutputStream(fs);
			os.writeObject(users);
			os.close();
			fs.close();
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    @FXML
    private void onClickExit(ActionEvent event) {
    	saveUserList();
    	System.exit(0);
    }

    @FXML
    private void onClickLogout(ActionEvent event) throws IOException {
    	saveUserList();
    	
    	// hides the previous window.
		((Node)event.getSource()).getScene().getWindow().hide();
		
		Stage primaryStage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/Loginfxml.fxml"));
		AnchorPane root = (AnchorPane)loader.load();
		LoginController con = (LoginController)(loader.getController());
		con.init();
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Photo Album Login Page");
		primaryStage.setResizable(false);  
		primaryStage.show();
    }
}