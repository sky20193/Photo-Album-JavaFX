package controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.*;

/**
 * Controller class for user view, where a user can view a list of existing albums,
 * create/delete albums, or go to the search view to search for photos, or go to
 * album view to manage an album.
 * 
 * @author Umang Patel
 * @author Akashkumar Patel
 */
public class UserAlbumController {
	@FXML private TextField tfCurrentUser;

    @FXML private TextField tfAlbumSelected;

    @FXML private TextField tfTotalPhotosInAlbum;

    @FXML private TextField tfStartDate;

    @FXML private TextField tfEndDate;
	
    @FXML private ListView<Album> lvAlbumDisplay;

    ObservableList<Album> albumObsList;
    User currentUser;
    
    /**
     * Initializes controller by loading in user data and setting up the appropriate list view listeners.
     */
    public void init(String username, Stage stage) {
    	try {
    		FileInputStream fs = new FileInputStream("data/"+username+".ser");
    		ObjectInputStream os = new ObjectInputStream(fs);
    		currentUser = (User)os.readObject();
    		os.close();
    		fs.close();
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		System.exit(0);
    	}
    	
    	stage.setOnCloseRequest(e -> this.saveUserData());
    	
    	tfCurrentUser.setText(currentUser.getName());
    	albumObsList = FXCollections.observableList(currentUser.getAlbums());
    	lvAlbumDisplay.setItems(albumObsList);
    	FXCollections.sort(albumObsList);
    	
    	lvAlbumDisplay.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Album>() {
    		public void changed(ObservableValue<? extends Album> a, Album old, Album next) {
    			if(next != null) {
    				tfAlbumSelected.setText(next.getName());
    				tfTotalPhotosInAlbum.setText(Integer.toString(next.getTotalPhotos()));
    				tfStartDate.setText(next.getStartDateString());
    				tfEndDate.setText(next.getEndDateString());
    			} else {
    				tfAlbumSelected.setText("");
    				tfTotalPhotosInAlbum.setText("");
    				tfStartDate.setText("");
    				tfEndDate.setText("");
    			}
    		}
    	});
    	
    	if(!albumObsList.isEmpty())
    		lvAlbumDisplay.getSelectionModel().select(0);
    }

    @FXML
    private void onCLickLogout(ActionEvent event) throws IOException {
    	saveUserData();
    	
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
    
    @FXML
    private void onClickCreateNewAlbum(ActionEvent event) throws FileNotFoundException {
    	TextInputDialog inputDialog = new TextInputDialog();
    	inputDialog.setTitle("Create Album");
    	inputDialog.setHeaderText("Enter the album name");
    	Optional<String> result = inputDialog.showAndWait();
    	
    	if(result.isPresent() && !result.get().equals("")) {
    		for(Album a: albumObsList) {
    			if(a.getName().equalsIgnoreCase(result.get())) {
    				Alert err = new Alert(Alert.AlertType.ERROR, "Album name is already in use.", ButtonType.OK);
    				err.showAndWait();
    				return;
    			}
    		}
       	}
    	else
    		return;
    	
    	//confirmation
    	Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to add the Album?", ButtonType.YES, ButtonType.CANCEL);
        confirm.showAndWait();
        if (confirm.getResult() != ButtonType.YES)
            return;
        
        Album newAlbum = new Album(result.get());
		albumObsList.add(newAlbum);
		FXCollections.sort(albumObsList);
		lvAlbumDisplay.getSelectionModel().select(newAlbum);
    }

    @FXML
    private void onClickDeleteAlbum(ActionEvent event) {
    	if(albumObsList.isEmpty()) {
    		Alert err = new Alert(Alert.AlertType.ERROR, "No Album to delete", ButtonType.OK);
    		err.showAndWait();
    		return;
    	}
    	
    	Album album = lvAlbumDisplay.getSelectionModel().getSelectedItem();
    	
    	Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + album.getName() +" ?", ButtonType.YES, ButtonType.CANCEL);
        confirm.showAndWait();
        if (confirm.getResult() != ButtonType.YES)
            return;
    	
    	int count = albumObsList.size();
    	int index = lvAlbumDisplay.getSelectionModel().getSelectedIndex();
    	
    	if(count-1 == index)
    		lvAlbumDisplay.getSelectionModel().selectPrevious();
    	else
    		lvAlbumDisplay.getSelectionModel().selectNext();
    	
    	albumObsList.remove(album);
    }

    @FXML
    private void onClickExit(ActionEvent event) {
    	saveUserData();
    	System.exit(0);
    }

    @FXML
    private void onClickRenameAlbum(ActionEvent event) {
    	if(albumObsList.isEmpty()) {
    		Alert err = new Alert(Alert.AlertType.ERROR, "No Album to rename", ButtonType.OK);
    		err.showAndWait();
    		return;
    	}
    	
    	TextInputDialog text = new TextInputDialog();
    	text.setTitle("Edit Album name");
    	text.setHeaderText("Enter new album name.");
    	Optional<String> result = text.showAndWait();
    	
    	if(result.isPresent() && !result.get().equals("")) {
    		Album currentAlbum = lvAlbumDisplay.getSelectionModel().getSelectedItem();
    		for(Album a: albumObsList) {
    			if(a.getName().equalsIgnoreCase(result.get())) {
    				Alert err = new Alert(Alert.AlertType.ERROR, "Album name is already in use.", ButtonType.OK);
    				err.showAndWait();
    				return;
    			}
    		}
    		
    		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to update the album name?", ButtonType.YES, ButtonType.CANCEL);
            confirm.showAndWait();
            if (confirm.getResult() != ButtonType.YES)
                return;
            
            currentAlbum.setName(result.get());
            FXCollections.sort(albumObsList);
            lvAlbumDisplay.getSelectionModel().select(currentAlbum);
    	}
    }

    @FXML
    private void onClickOpenAlbum(ActionEvent event) throws IOException {
    	if(albumObsList.isEmpty()) {
    		Alert err = new Alert(Alert.AlertType.ERROR, "No Album to open", ButtonType.OK);
    		err.showAndWait();
    		return;
    	}
    	
    	saveUserData();
    	Album currentAlbum = lvAlbumDisplay.getSelectionModel().getSelectedItem();
    	((Node)event.getSource()).getScene().getWindow().hide();
    	Stage albumStage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/InsideAlbum.fxml"));
		AnchorPane root = (AnchorPane)loader.load();
		InsideAlbumController con = (InsideAlbumController)(loader.getController());
		con.init(currentUser.getName(), currentAlbum.getName(), albumStage);
		Scene scene = new Scene(root);
		albumStage.setScene(scene);
		albumStage.setTitle("Photo Album Page");
		albumStage.setResizable(false);  
		albumStage.show();
    }
    
    @FXML
    private void onClickSearch(ActionEvent event) throws IOException {
    	saveUserData();
    	((Node)event.getSource()).getScene().getWindow().hide();
    	Stage searchStage = new Stage();
    	FXMLLoader loader = new FXMLLoader();
    	loader.setLocation(getClass().getResource("/view/Searchfxml.fxml"));
		AnchorPane root = (AnchorPane)loader.load();
		SearchController con = (SearchController)(loader.getController());
		con.init(currentUser.getName(), searchStage);
		Scene scene = new Scene(root);
		searchStage.setScene(scene);
		searchStage.setTitle("Search Page");
		searchStage.setResizable(false);
		searchStage.show();
    }
    
    private void saveUserData() {
    	try {
    		FileOutputStream fs = new FileOutputStream("data/"+currentUser.getName()+".ser");
			ObjectOutputStream os = new ObjectOutputStream(fs);
			os.writeObject(currentUser);
			os.close();
			fs.close();
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    }
}