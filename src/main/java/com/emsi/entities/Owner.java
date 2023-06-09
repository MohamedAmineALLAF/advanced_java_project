package com.emsi.entities;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Owner implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String CIN;
    private String address;
    private Integer phoneNumber;
    //export to services
    //write from database
    //check input type from int or date ??
    //date enters as date or string ??
    //save and update in one method(check and save or update)
    
}
