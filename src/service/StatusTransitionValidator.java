package service;

import model.IncidentStatus;
import exception.InvalidTransitionException;

public class StatusTransitionValidator {
    public static void validate(IncidentStatus current, IncidentStatus next) throws InvalidTransitionException {
        if (current == IncidentStatus.KAPATILDI && next != IncidentStatus.KAPATILDI) {
            throw new InvalidTransitionException("Kapatılmış bir olay tekrar açılamaz.");
        }
    }
}
