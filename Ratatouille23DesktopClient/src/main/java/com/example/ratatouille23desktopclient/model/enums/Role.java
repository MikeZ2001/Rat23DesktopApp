package com.example.ratatouille23desktopclient.model.enums;

public enum Role {
    WAITER,
    CHEF,
    SUPERVISOR,
    ADMINISTRATOR;

    public String toString(){
        if (this.equals(CHEF))
            return "Addetto alla cucina";
        if (this.equals(WAITER))
            return "Addetto alla sala";
        if (this.equals(SUPERVISOR))
            return "Supervisore";
        if (this.equals(ADMINISTRATOR))
            return "Amministratore";
        return null;
    }
}
