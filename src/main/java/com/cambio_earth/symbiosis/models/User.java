package com.cambio_earth.symbiosis.models;

import jakarta.persistence.*;

// TODO: Add private uid
// TODO: Add private permissions (will this be enum or bool since its 2 options (they have or dont))


public class User {
    private String name;
    private String email;
    private String password;
}
