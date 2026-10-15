package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "CONTACTOS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Contacto extends EntityId {

    private String email;
    private String telefono;
    private String celular;

    public Contacto(Long id, String email, String telefono, String celular) {
        super(id);
        this.email = email;
        this.telefono = telefono;
        this.celular = celular;
    }
}
