package pl.luczak.michal.joboffersapp.offer;

import lombok.NonNull;

import java.util.UUID;

public class OfferNotFoundException extends RuntimeException {

    public static final String MESSAGE = "Offer with uniqueID: %s not found";

    public OfferNotFoundException(@NonNull UUID uniqueID) {
        super(String.format(MESSAGE, uniqueID));
    }
}
