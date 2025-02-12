package peoplesoft.ui.regions;

import java.util.List;
import java.util.Objects;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import peoplesoft.model.job.Job;
import peoplesoft.model.person.Person;
import peoplesoft.ui.UiPart;

/**
 * A UI component that displays information of a {@code Job}.
 */
public class JobCard extends UiPart<Region> {

    private static final String FXML = "JobListCard.fxml";

    public final Job job;

    private final Image cross = new Image(Objects.requireNonNull(this.getClass()
            .getResourceAsStream("/images/apple-cross-emoji.png")));
    private final Image tick = new Image(Objects.requireNonNull(this.getClass()
            .getResourceAsStream("/images/apple-tick-emoji.png")));

    @FXML
    private HBox cardPane;
    @FXML
    private Label idx; // displayed index, not job ID
    @FXML
    private Label desc;
    @FXML
    private Label duration;
    @FXML
    private StackPane doneIconCol;
    @FXML
    private ImageView doneIcon; // false
    @FXML
    private ImageView paidForIcon; // false
    @FXML
    private StackPane paidForIconCol;
    @FXML
    private FlowPane assigned;

    /**
     * Creates a {@code PersonCode} with the given {@code Person} and index to display.
     */
    public JobCard(Job job, int displayedIndex, List<Person> assignedPeople,
            List<ReadOnlyDoubleProperty> colWidths) {
        super(FXML);

        // dynamically size the card
        List<? extends Region> cols = List.of(idx, desc, duration, doneIconCol,
                paidForIconCol, assigned);
        if (cols.size() != colWidths.size()) {
            throw new RuntimeException("JobCard colWidths count != cols count");
        }
        for (int i = 0; i < cols.size(); i++) {
            cols.get(i).minWidthProperty().bind(colWidths.get(i));
            cols.get(i).maxWidthProperty().bind(colWidths.get(i));
        }

        // calculate duration to display
        int hH = job.getDuration().toHoursPart();
        int mins = job.getDuration().toMinutesPart();
        String mM = mins == 0 ? "" : (mins + "m");

        int one = 1;

        // assign values
        this.job = job;
        idx.setText(displayedIndex + "");
        desc.setText(job.getDesc());
        duration.setText(String.format("%dh %s", hH, mM));
        doneIcon.setImage(job.hasPaid() ? tick : cross);
        paidForIcon.setImage(job.isFinal() ? tick : cross);

        // Display people assigned to the job
        ReadOnlyDoubleProperty asgnPaneWidthProperty = assigned.widthProperty();
        ObservableList<Node> visibleAssignemnts = assigned.getChildren();

        assignedPeople.forEach(person -> {
            Label lbl = new Label(person.getName() + "");
            lbl.setWrapText(true);
            lbl.setTextAlignment(TextAlignment.LEFT);
            lbl.maxWidthProperty().bind(asgnPaneWidthProperty);
            visibleAssignemnts.add(lbl);
        });
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof JobCard)) {
            return false;
        }

        // state check
        JobCard card = (JobCard) other;
        assert idx != null;
        assert card.idx != null;

        return idx.getText().equals(card.idx.getText()) // same display id, not job ID
                && Objects.equals(job, card.job);
    }
}
