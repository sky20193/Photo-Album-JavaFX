package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import model.*;

/**
 * Controller class for album view where a user can add/delete photos from an album,
 * copy/move photos between albums, start a slideshow to view photos, or view a single photos.
 * 
 * @author Umang Patel
 * @author Akashkumar Patel
 */
public class InsideAlbumController {

    @FXML
    private ListView<Photo> lvPhotos;

    @FXML
    private TextField tfTagType;

    @FXML
    private TextField tfTagValue;

    @FXML
    private ListView<Tag> lvTags;
    
    ObservableList<Photo> photoObsList;
    ObservableList<Tag> tagObsList;
    User currentUser;
    Album currentAlbum;
    Stage currentStage;
    
    /**
     * Initializes controller by loading in user information and information about 
     * the album that was opened, as well as setting up the appropriate list views.
     */
    public void init(String username, String albumname, Stage stage) {
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
    	
    	for(Album a: currentUser.getAlbums())
    		if(a.getName().equals(albumname)) {
    			currentAlbum = a;
    			break;
    		}
    	
    	stage.setOnCloseRequest(e -> this.saveUserData());
    	currentStage = stage;
    	
    	photoObsList = FXCollections.observableList(currentAlbum.getPhotos());
    	lvPhotos.setItems(photoObsList);
    	FXCollections.sort(photoObsList);
    	tagObsList = FXCollections.observableArrayList();
    	lvTags.setItems(tagObsList);
    	
    	lvPhotos.setCellFactory(new Callback<ListView<Photo>, ListCell<Photo>>() {
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
    	
    	lvPhotos.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Photo>() {
    		public void changed(ObservableValue<? extends Photo> p, Photo old, Photo next) {
    			if(next == null) {
    				tagObsList = FXCollections.observableList(new ArrayList<Tag>());
    				lvTags.setItems(tagObsList);
    			}
    			else {
    				tagObsList = FXCollections.observableList(next.getTags());
    				lvTags.setItems(tagObsList);
    				FXCollections.sort(tagObsList);
    			}
    		}
    	});
    	
    	lvTags.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tag>() {
    		public void changed(ObservableValue<? extends Tag> t, Tag old, Tag next) {
    			if(next != null) {
    				tfTagType.setText(next.getType());
    				tfTagValue.setText(next.getValue());
    			} else {
    				tfTagType.setText("");
    				tfTagValue.setText("");
    			}
    		}
    	});
    	
    	if(!photoObsList.isEmpty())
    		lvPhotos.getSelectionModel().select(0);
    	
    	if(!tagObsList.isEmpty())
    		lvTags.getSelectionModel().select(0);
    }

    @FXML
    private void onClickAddPhoto(ActionEvent event) throws MalformedURLException {
    	FileChooser chooser = new FileChooser();
    	chooser.setTitle("Choose photo to add");
    	chooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
    	File selectedFile = chooser.showOpenDialog(currentStage);
    	
    	if(selectedFile == null) 
    		return;
    	String path = selectedFile.toURI().toURL().toExternalForm();
    	for(Photo p: currentAlbum.getPhotos()) {
    		if(p.getFilename().equals(path)) {
    			Alert err = new Alert(Alert.AlertType.ERROR, "Photo already exists in album.", ButtonType.OK);
				err.showAndWait();
				return;
    		}
    	}
    	
    	Photo newPhoto = new Photo(path);
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(selectedFile.lastModified());
    	cal.set(Calendar.MILLISECOND, 0);
    	newPhoto.setDateTime(cal);
    	
    	Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to add the photo?", ButtonType.YES, ButtonType.CANCEL);
        confirm.showAndWait();
        if (confirm.getResult() != ButtonType.YES)
            return;
        
        photoObsList.add(newPhoto);
        FXCollections.sort(photoObsList);
        lvPhotos.getSelectionModel().select(newPhoto);
        currentAlbum.recalculateDates();
    }

    @FXML
    private void onClickAddTag(ActionEvent event) {
    	if(photoObsList.isEmpty()) {
    		Alert err = new Alert(Alert.AlertType.ERROR, "No Photo to add to", ButtonType.OK);
    		err.showAndWait();
    		return;
    	}
    	
    	if (tfTagType.getText().trim().equals("") || tfTagValue.getText().trim().equals("")) {
            Alert err = new Alert(Alert.AlertType.ERROR, "type and/or value is empty.", ButtonType.OK);
            err.showAndWait();
            return;
        }
    	
    	Photo current = lvPhotos.getSelectionModel().getSelectedItem();
    	Tag newTag = new Tag(tfTagType.getText(), tfTagValue.getText());
    	
    	for(Tag t: current.getTags())
    		if(t.equals(newTag)) {
    			Alert err = new Alert(Alert.AlertType.ERROR, "Tag already exists", ButtonType.OK);
				err.showAndWait();
				return;
    		}
    	
    	Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to add this tag?", ButtonType.YES, ButtonType.CANCEL);
        confirm.showAndWait();
        if (confirm.getResult() != ButtonType.YES)
            return;
        
        tagObsList.add(newTag);
        FXCollections.sort(tagObsList);
        lvTags.getSelectionModel().select(newTag);
    }

    @FXML
    private void onClickBack(ActionEvent event) throws IOException {
    	saveUserData();
    	((Node)event.getSource()).getScene().getWindow().hide();
		
		Stage userStage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/UserAlbum.fxml"));
		AnchorPane root = (AnchorPane)loader.load();
		UserAlbumController con = (UserAlbumController)loader.getController();
		con.init(currentUser.getName(), userStage);
		Scene scene = new Scene(root);
		userStage.setScene(scene);
		userStage.initStyle(StageStyle.DECORATED);
		userStage.setTitle("User Page");
		userStage.setResizable(false);
		userStage.show();
    }

    @FXML
    private void onClickCopyPhoto(ActionEvent event) {
    	if(photoObsList.isEmpty() || currentUser.getAlbums().size() == 1) {
    		Alert err = new Alert(Alert.AlertType.ERROR, "No album to copy to or no photo", ButtonType.OK);
    		err.showAndWait();
    		return;
    	}
    	
    	List<Album> choiceList = new ArrayList<Album>(currentUser.getAlbums());
    	choiceList.remove(currentAlbum);
    	ChoiceDialog<Album> dialog = new ChoiceDialog<Album>(choiceList.get(0), choiceList);
    	dialog.setTitle("Copy photo to other album");
    	dialog.setContentText("Copy to:");
    	Optional<Album> result = dialog.showAndWait();
    	
    	if(result.isPresent()) {
    		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to copy this photo to another album?", ButtonType.YES, ButtonType.CANCEL);
            confirm.showAndWait();
            if (confirm.getResult() != ButtonType.YES)
                return;
            
            result.get().addPhoto(lvPhotos.getSelectionModel().getSelectedItem());
    	}
    }

    @FXML
    private void onClickDeleteTag(ActionEvent event) {
    	if(tagObsList.isEmpty()) {
    		Alert err = new Alert(Alert.AlertType.ERROR, "No tag to delete", ButtonType.OK);
    		err.showAndWait();
    		return;
    	}
    	
    	Tag t = lvTags.getSelectionModel().getSelectedItem();
    	
    	Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this tag?", ButtonType.YES, ButtonType.CANCEL);
        confirm.showAndWait();
        if (confirm.getResult() != ButtonType.YES)
            return;
        
        int count = tagObsList.size();
    	int index = lvTags.getSelectionModel().getSelectedIndex();
    	
    	if(count-1 == index)
    		lvTags.getSelectionModel().selectPrevious();
    	else
    		lvTags.getSelectionModel().selectNext();
    	
    	tagObsList.remove(t);
    }

    @FXML
    private void onClickDisplayPhoto(ActionEvent event) throws IOException {
    	if(photoObsList.isEmpty()) {
    		Alert err = new Alert(Alert.AlertType.ERROR, "No Photo to display", ButtonType.OK);
    		err.showAndWait();
    		return;
    	}
    	Photo p = lvPhotos.getSelectionModel().getSelectedItem();
    	Stage primaryStage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/PhotoDisplay.fxml"));
		AnchorPane root = (AnchorPane)loader.load();
		PhotoDisplayController con = (PhotoDisplayController)(loader.getController());
		con.init(p);
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Photo");
		primaryStage.setResizable(true);
		primaryStage.show();
    }

    @FXML
    private void onClickEditCaption(ActionEvent event) {
    	if(photoObsList.isEmpty()) {
    		Alert err = new Alert(Alert.AlertType.ERROR, "No Photo to edit", ButtonType.OK);
    		err.showAndWait();
    		return;
    	}
    	
    	TextInputDialog inputDialog = new TextInputDialog();
    	inputDialog.setTitle("Edit Caption");
    	inputDialog.setHeaderText("Enter the new caption");
    	Optional<String> result = inputDialog.showAndWait();
    	
    	if(result.isPresent()) {
	    	if(result.get().equals("")) {
				Alert err = new Alert(Alert.AlertType.ERROR, "Invalid Caption", ButtonType.OK);
				err.showAndWait();
				return;
			}
	    	
	    	Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to edit the caption?", ButtonType.YES, ButtonType.CANCEL);
            confirm.showAndWait();
            if (confirm.getResult() != ButtonType.YES)
                return;
            
            lvPhotos.getSelectionModel().getSelectedItem().setCaption(result.get());
            Photo p = lvPhotos.getSelectionModel().getSelectedItem();
            photoObsList.remove(p);
            photoObsList.add(p);
            FXCollections.sort(photoObsList);
            lvPhotos.getSelectionModel().select(p);
    	}
    }

    @FXML
    private void onClickExit(ActionEvent event) {
    	saveUserData();
    	System.exit(0);
    }

    @FXML
    private void onClickLogout(ActionEvent event) throws IOException {
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
    private void onClickMovePhoto(ActionEvent event) {
    	if(photoObsList.isEmpty() || currentUser.getAlbums().size() == 1) {
    		Alert err = new Alert(Alert.AlertType.ERROR, "No Photo to move or no albums to move to", ButtonType.OK);
    		err.showAndWait();
    		return;
    	}
    	
    	List<Album> choiceList = new ArrayList<Album>(currentUser.getAlbums());
    	choiceList.remove(currentAlbum);
    	ChoiceDialog<Album> dialog = new ChoiceDialog<Album>(choiceList.get(0), choiceList);
    	dialog.setTitle("Move photo to other album");
    	dialog.setContentText("Move to:");
    	Optional<Album> result = dialog.showAndWait();
    	
    	if(result.isPresent()) {
    		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to move this photo to another album?", ButtonType.YES, ButtonType.CANCEL);
            confirm.showAndWait();
            if (confirm.getResult() != ButtonType.YES)
                return;
            
            Photo p = lvPhotos.getSelectionModel().getSelectedItem();
            result.get().addPhoto(p);
            photoObsList.remove(p);
    	}
    }

    @FXML
    private void onClickRemovePhoto(ActionEvent event) {
    	if(photoObsList.isEmpty()) {
    		Alert err = new Alert(Alert.AlertType.ERROR, "No Photo to delete", ButtonType.OK);
    		err.showAndWait();
    		return;
    	}
    	
    	Photo photo = lvPhotos.getSelectionModel().getSelectedItem();
    	
    	Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this photo?", ButtonType.YES, ButtonType.CANCEL);
        confirm.showAndWait();
        if (confirm.getResult() != ButtonType.YES)
            return;
        
        int count = photoObsList.size();
    	int index = lvPhotos.getSelectionModel().getSelectedIndex();
    	
    	if(count-1 == index)
    		lvPhotos.getSelectionModel().selectPrevious();
    	else
    		lvPhotos.getSelectionModel().selectNext();
    	
    	photoObsList.remove(photo);
    	currentAlbum.recalculateDates();
    }

    @FXML
    private void onClickStartSlideshow(ActionEvent event) throws IOException {
    	if(photoObsList.isEmpty()) {
    		Alert err = new Alert(Alert.AlertType.ERROR, "No Photos to display", ButtonType.OK);
    		err.showAndWait();
    		return;
    	}
    	
    	Stage primaryStage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/Slideshowfxml.fxml"));
		AnchorPane root = (AnchorPane)loader.load();
		SlideShowController con = (SlideShowController)(loader.getController());
		con.init(currentAlbum.getPhotos());
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Slideshow");
		primaryStage.setResizable(true);  
		primaryStage.showAndWait();
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