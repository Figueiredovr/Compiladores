package model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 *
 * @author Victor Figueiredo
 */
public class Metodo {

  public String nome_metodo;
  public String tipo_da_metodo;
  public List parametro =  new ArrayList();

  public Metodo (  String tipo_da_metodo, String nome_metodo){
    this.tipo_da_metodo = tipo_da_metodo;
    this.nome_metodo = nome_metodo;
  }

  public void add_parametro (Var parametro){
    this.parametro.add(parametro);
  }

}
