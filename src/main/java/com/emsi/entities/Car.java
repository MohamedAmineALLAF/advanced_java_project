package com.emsi.entities;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Car implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String brand;
    private String model;
    private String registrationNumber;
    private Date registrationDate;
    private String color;
    private FuelType fuelType;//fix enum
    private Owner owner;

    public enum FuelType {
        GASOLINE,
        DIESEL,
        ELECTRIC,
        HYBRID
    }

}
