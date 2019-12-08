package controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.*;

/**
 * Controller class for search view where a user can search for photos in
 * all their albums by either date or by tag. A new album can be created from the search results.
 * 
 * @author Umang Patel
 * @author Akashkumar Patel
 */
public class SearchController {

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private GridPane tagsGridPane;
    
    @FXML
    private TextField tagTypeTF;
    
    @FXML
    private TextField tagValueTF;

    @FXML
    private ListView<Photo> lvSearchResults;
    
    User currentUser;
    List<Photo> searchResults;
    ObservableList<Photo> searchObsList;
    
    /**
     * Initializes controller by loading in user information and setting up appropriate widgets.
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
    	
    	startDatePicker.setValue(LocalDate.now());
    	endDatePicker.setValue(LocalDate.now());
    	
    	searchResults = new ArrayList<Photo>();
    	searchObsList = FXCollections.observableList(searchResults);
    	lvSearchResults.setItems(searchObsList);
    	
    	lvSearchResults.setCellFactory(new Callback<ListView<Photo>, ListCell<Photo>>() {
    		public ListCell<Photo> call(ListView<Photo> param) {
    			ListCell<Photo> cell = new ListCell<Photo>() {
		    		ImageView thumbnail = new ImageView();
		    		public void updateItem(Photo photo, boolean empty) {
		    			super.updateItem(photo, empty);
		    			if(photo == null) {
		    				setText(null);
		    				setGraphic(null);
		    			} else {
		    				Image thumb = new Image(photo.getFilename(), 100, 100, false, false);
		    				thumbnail.setImage(thumb);
		    				setText(photo.getCaption());
		    				setGraphic(thumbnail);
		    			}
		    		}
    			};
    			return cell;
    		}
    	});
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

    @FXML
    private void onClickCloseSearch(ActionEvent event) throws IOException {
    	saveUserData();
    	((Node)event.getSource()).getScene().getWindow().hide();
    	Stage userStage = new Stage();
    	FXMLLoader loader = new FXMLLoader();
    	loader.setLocation(getClass().getResource("/view/UserAlbum.fxml"));
		AnchorPane root = (AnchorPane)loader.load();
		UserAlbumController con = (UserAlbumController)(loader.getController());
		con.init(currentUser.getName(), userStage);
		Scene scene = new Scene(root);
		userStage.setScene(scene);
		userStage.setTitle("User Page");
		userStage.setResizable(false);
		userStage.show();
    }

    @FXML
    private void onClickCreateNewAlbum(ActionEvent event) {
    	if(searchObsList.isEmpty()) {
    		Alert err = new Alert(Alert.AlertType.ERROR, "No Photos to create album with", ButtonType.OK);
    		err.showAndWait();
    		return;
    	}
    	
    	TextInputDialog inputDialog = new TextInputDialog();
    	inputDialog.setTitle("Create Album");
    	inputDialog.setHeaderText("Enter the album name");
    	Optional<String> result = inputDialog.showAndWait();
    	
    	if(result.isPresent() && !result.get().equals("")) {
    		for(Album a: currentUser.getAlbums()) {
    			if(a.getName().equalsIgnoreCase(result.get())) {
    				Alert err = new Alert(Alert.AlertType.ERROR, "Album name is already in use.", ButtonType.OK);
    				err.showAndWait();
    				return;
    			}
    		}
       	}
    	else
    		return;
    	
    	Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to create the Album?", ButtonType.YES, ButtonType.CANCEL);
        confirm.showAndWait();
        if (confirm.getResult() != ButtonType.YES)
            return;
        
        Album newAlbum = new Album(result.get(), searchResults);
        currentUser.addAlbum(newAlbum);
    }

    @FXML
    private void onClickSearchByDate(ActionEvent event) {
    	LocalDate start = startDatePicker.getValue();
    	LocalDate end = endDatePicker.getValue();
    	
    	if(end.isBefore(start)) {
    		Alert err = new Alert(Alert.AlertType.ERROR, "End date cant be before start date.", ButtonType.OK);
			err.showAndWait();
			return;
    	}
    	
    	LocalDate photoLocalDate;
    	Date photoDate;
    	searchObsList.clear();
    	
    	for(Album a: currentUser.getAlbums()) {
    		for (Photo p: a.getPhotos()) {
    			photoDate = p.getDateTime().getTime();
    			photoLocalDate = photoDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    			
    			if(photoLocalDate.isBefore(end) && photoLocalDate.isAfter(start))
    				searchObsList.add(p);
    		}
    	}
    }

    @FXML
    private void onClickSearchByTag(ActionEvent event) {
    	if(tagTypeTF.getText().equals("") || tagValueTF.getText().equals("")) {
    		return;
    	}
    	
    	Tag t = new Tag(tagTypeTF.getText(), tagValueTF.getText());
    	searchObsList.clear();
    	
    	for(Album a: currentUser.getAlbums()) {
    		for(Photo p: a.getPhotos()) {
    			for(Tag tag: p.getTags()) {
    				if(t.equals(tag)) {
    					searchObsList.add(p);
    					break;
    				}
    			}
    		}
    	}
    }
}