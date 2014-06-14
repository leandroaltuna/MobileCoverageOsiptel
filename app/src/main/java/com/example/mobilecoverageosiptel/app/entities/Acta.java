package com.example.mobilecoverageosiptel.app.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Acta {

    @Expose()
    @SerializedName("nombre")
    public String nombre;

    @Expose()
    @SerializedName("latitud")
    public String latitud;

    @Expose()
    @SerializedName("longitud")
    public String longitud;

    @Expose()
    @SerializedName("ccdd")
    public String ccdd;

    @Expose()
    @SerializedName("departamento")
    public String departamento;

    @Expose()
    @SerializedName("ccpp")
    public String ccpp;

    @Expose()
    @SerializedName("provincia")
    public String provincia;

    @Expose()
    @SerializedName("cdi")
    public String cdi;

    @Expose()
    @SerializedName("distrito")
    public String distrito;

    @Expose()
    @SerializedName("co_lo")
    public String co_lo;

    @Expose()
    @SerializedName("localidad")
    public String localidad;

    @Expose()
    @SerializedName("co_empresa")
    public String co_empresa;

    @Expose()
    @SerializedName("empresa")
    public String empresa;

    @Expose()
    @SerializedName("fecha")
    public String fecha;

    @Expose()
    @SerializedName("hora_inicio")
    public String hora_inicio;

    @Expose()
    @SerializedName("hora_fin")
    public String hora_fin;

    @Expose()
    @SerializedName("marca")
    public String marca;

    @Expose()
    @SerializedName("modelo")
    public String modelo;

    @Expose()
    @SerializedName("sistema_o")
    public String sistema_o;

    @Expose()
    @SerializedName("nu_telefono")
    public String nu_telefono;

    @Expose()
    @SerializedName("claro")
    public String claro;

    @Expose()
    @SerializedName("movistar")
    public String movistar;

    @Expose()
    @SerializedName("nextel")
    public String nextel;

}