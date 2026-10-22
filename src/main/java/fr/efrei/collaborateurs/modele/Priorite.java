package fr.efrei.collaborateurs.modele;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "ticket")

public enum Priorite { BASSE, NORMALE, HAUTE }
