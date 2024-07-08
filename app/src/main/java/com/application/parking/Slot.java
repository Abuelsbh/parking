package com.application.parking;

public class Slot {
    String Slot;
    boolean Status = false;

    public Slot(String slot, boolean status) {
        Slot = slot;
        Status = status;
    }

    public void setSlot(String slot) {
        Slot = slot;
    }

    public void setStatus(boolean status) {
        Status = status;
    }

    public String getSlot() {
        return Slot;
    }

    public boolean isStatus() {
        return Status;
    }
}
