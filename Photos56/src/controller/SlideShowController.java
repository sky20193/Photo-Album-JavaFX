package controller;

import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Photo;

/**
 * Controller for slideshow view where photos can be viewed in sequence. Only the image
 * is displayed, no other information such as date or caption is displayed in this view.
 * 
 * @author Umang Patel
 * @author Akashkumar Patel
 */
public class SlideShowController {

    @FXML
    private ImageView imageViewer;
    
    List<Photo> photoList;
    Photo currentPhoto;
    
    /**
     * Initializes the view using a list of photos to display.
     */
    public void init(List<Photo> photos) {
    	photoList = photos;
    	currentPhoto = photoList.get(0);
    	Image photo = new Image(currentPhoto.getFilename());
    	imageViewer.setImage(photo);
    }

    @FXML
    private void OnClickBackToPhotos(ActionEvent event) {
    	((Node)event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    private void onClickNext(ActionEvent event) {
    	int index = photoList.indexOf(currentPhoto);
    	if(index+1 >= photoList.size())
    		index = 0;
    	else
    		index++;
    	
    	currentPhoto = photoList.get(index);
    	Image photo = new Image(currentPhoto.getFilename());
    	imageViewer.setImage(photo);
    }

    @FXML
    private void onClickPrevious(ActionEvent event) {
    	int index = photoList.indexOf(currentPhoto);
    	if(index-1 < 0)
    		index = photoList.size()-1;
    	else
    		index--;
    	
    	currentPhoto = photoList.get(index);
    	Image photo = new Image(currentPhoto.getFilename());
    	imageViewer.setImage(photo);
    }
}