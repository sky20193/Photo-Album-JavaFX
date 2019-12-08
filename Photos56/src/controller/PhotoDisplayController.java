package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Photo;
import model.Tag;

/**
 * Controller class for photo view, where a single photo is displayed
 * along with its relevant information.
 * 
 * @author Umang Patel
 * @author Akashkumar Patel
 */
public class PhotoDisplayController {

    @FXML
    private ImageView imgView;

    @FXML
    private Label captionLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private ListView<Tag> tagLV;
    
    /**
     * Initializes view using appropriate information from the Photo instance.
     */
    public void init(Photo photo) {
    	captionLabel.setText(photo.getCaption());
    	dateLabel.setText(photo.getDateTimeString());
    	Image img = new Image(photo.getFilename());
    	imgView.setImage(img);
    	ObservableList<Tag> tagObsList = FXCollections.observableList(photo.getTags());
    	tagLV.setItems(tagObsList);
    }
}