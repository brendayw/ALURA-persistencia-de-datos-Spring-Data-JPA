package com.aluracursos.screenmatch.service;

public interface IConviertoDatos {
    <T> T obtenerDatos(String json, Class<T> clase);

}
