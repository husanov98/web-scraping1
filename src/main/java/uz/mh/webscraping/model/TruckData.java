package uz.mh.webscraping.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TruckData {
    private String truckNumber;
    private String exitDate;
    private String customEnterPost;
    private String bookNumber;
    private String customsExitPost;
    private String enterDate;

    private String senderName;
    private String receiverName;

    public TruckData(String number) {
        this.truckNumber = number;
    }
}