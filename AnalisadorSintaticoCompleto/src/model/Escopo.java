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
public class Escopo {


  public Escopo pai;
  public List variaveis =  new ArrayList();
  public List metodos =  new ArrayList();

  public Escopo (){}

  public Escopo (Escopo anterior){
    this.pai = anterior;

  }



}
