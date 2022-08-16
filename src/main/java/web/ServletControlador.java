/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import datos.ClienteDao;
import dominio.Cliente;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Fernando Davila
 */
@WebServlet(name = "ServletControlador", urlPatterns = {"/ServletControlador"})
public class ServletControlador extends HttpServlet {

    private double calcularSaldoTotal(List<Cliente> clientes) {
        double saldoTotal = 0;
        for (Cliente cliente : clientes) {
            saldoTotal += cliente.getSaldo();
        }
        return saldoTotal;
    }
    
    private void eliminarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        int idCliente = 0;
        String idString = request.getParameter("idCliente");
        if(idString != null && !idString.equals("")){
            idCliente = Integer.parseInt(idString);
        }
      
        //no incluir id porque se define en la db
        Cliente cliente = new Cliente(idCliente);
        //eliminar en la db
        int registrosModificados= new ClienteDao().eliminar(cliente);
        System.out.println(registrosModificados);
        //redirigir a accion default
        this.accionDefault(request, response);
    }
    
    private void modificarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        
        //recuperar valores del formulario agregar cliente
        int idCliente = 0;
        String idString = request.getParameter("idCliente");
        if(idString != null && !idString.equals("")){
            idCliente = Integer.parseInt(idString);
        }
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String email = request.getParameter("email");
        String telefono = request.getParameter("telefono");
        double saldo = 0;
        String saldoSting = request.getParameter("saldo");
        
        //se valida para evitar una futura excepcion al momento de parsear 
        if (saldoSting != null && !saldoSting.equals("")){
            saldo = Double.parseDouble(saldoSting);
        }
        //no incluir id porque se define en la db
        Cliente cliente = new Cliente(idCliente, nombre, apellido, email, telefono, saldo);
        //insertar en la db
        int registrosModificados= new ClienteDao().actualizar(cliente);
        System.out.println(registrosModificados);
        //redirigir a accion default
        this.accionDefault(request, response);
    }
    
    private void editarCliente(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException{
        
        if(!request.getParameter("idCliente").equals("") && request.getParameter("idCliente") != null){
            int idCliente = Integer.parseInt(request.getParameter("idCliente"));
            Cliente cliente = new ClienteDao().encontrar(new Cliente(idCliente));
            request.setAttribute("cliente", cliente);
            String jspEditar = "/WEB-INF/paginas/cliente/editarCliente.jsp";
            request.getRequestDispatcher(jspEditar).forward(request, response);
            
        }
    }
    
    private void accionDefault(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Cliente> clientes = new ClienteDao().listar();
        System.out.println("Clientes = " + clientes);
        HttpSession sesion= request.getSession();
        sesion.setAttribute("clientes", clientes);
        sesion.setAttribute("totalClientes", clientes.size());
        sesion.setAttribute("saldoTotal", this.calcularSaldoTotal(clientes));
        //request.getRequestDispatcher("clientes.jsp").forward(request, response);
        response.sendRedirect("clientes.jsp");
        
    }
    
    private void insertarCliente(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException{
        
        //recuperar valores del formulario agregar cliente
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String email = request.getParameter("email");
        String telefono = request.getParameter("telefono");
        double saldo = 0;
        String saldoSting = request.getParameter("saldo");
        
        //se valida para evitar una futura excepcion al momento de parsear 
        if (saldoSting != null && !saldoSting.equals("")){
            saldo = Double.parseDouble(saldoSting);
        }
        //no incluir id porque se define en la db
        Cliente cliente = new Cliente(nombre, apellido, email, telefono, saldo);
        //insertar en la db
        int registrosModificados= new ClienteDao().insertar(cliente);
        System.out.println(registrosModificados);
        //redirigir a accion default
        this.accionDefault(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if(accion != null){
            switch(accion){
                case "editar":
                    this.editarCliente(request, response);
                    break;
                case "eliminar":
                    this.eliminarCliente(request, response);
                    break;
                default:
                    this.accionDefault(request, response);
            }
        } else{
            this.accionDefault(request, response);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion != null && !accion.equals("")){
            switch (accion) {
                case "insertar":
                    this.insertarCliente(request, response);
                    break;
                case "modificar":
                    this.modificarCliente(request, response);
                    break;
                default:
                    this.accionDefault(request, response);
            }
        }else {
            this.accionDefault(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
