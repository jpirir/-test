package net.GU.seguros.inspeccion;

//P-RC-15-R-01-V1


import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.seguridad.Usuario;

import utilidades.conecciones.oracle.Portal;

import utilidades.mail.SendError;


public class ReimpresionInspeccion2 extends HttpServlet {

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/pdf");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc_pdf = new Document(PageSize.LETTER, 1, /*izquierda*/1, /*derecha*/50, /*arriba*/1); /*abajo*/

        String nombre = "";
        String cod_contacto = "";
        String nit = "";
        String telefono = " ";
        String cvemarca = "";
        String cveestil = "";
        String cveuso = "";
        String cvecolor = "";
        String cvetvehi = "";
        String anio = "";
        String placas = "";
        String kilometros = "";
        String chasis = "";
        String motor = "";
        String transmision = "";
        String cod_contacto2 = "";
        String cveage = "";
        String sumaseg ="";
        String sumasegAuth ="";
        String km_mi = "";
        String procedencia="";
        String clave = "inspSguni1879!%";
        String id_inspeccion = request.getParameter("ID_INSPECCION").trim();

        response.setHeader("Content-Disposition", "filename=Inspeccion_" + id_inspeccion + ".pdf");

        String str_marca = "";
        String str_estilo = "";
        String str_tipovehiculo = "";
        String str_color = "";
        String str_direccion = "";
        String str_fecha = "";

        String nombre_inspector = "";

        String cadena = "";

        String OBSERVACIONES = "";
        Portal portal = new Portal();
        try {
            ResultSet rs = null;

            String consulta =
                "SELECT a.INSPECCION ID_INSPECCION," + " nvl(to_char(a.inspector),'- inspeccion web -') COD_CONTACTO  ," +
                " a.CVEMARCA ," + " a.CVEESTILo CVEESTIL," + " nvl(a.CVENIT, '---') NIT," +
                " b.numero_celular TELEFONO," + " a.modelveh ANIO ," + " a.CVEUSO ," + " a.PLACAS ," +
                " a.KILOMETROS ," + " a.CVETVEHI ," + " a.CVECOLOR ," + " a.CHASsIS CHASIS, " + " a.MOTOR ," +
                " a.TRANSMISION ," + " nvl(to_char(a.COD_CONTACTO), '-1') COD_CONTACTO2 , " + " a.CVEAGE ," +
                " a.fchinspeccion FECHA, " +
                " nvl(a.nombre1,' ') || ' ' || nvl(a.apellido1,' ') nombre_contacto, " +
                " nvl(a.OBSERVACIONES, '/*--*/')  OBSERVACIONES " +
                " ,nvl(pkg_general.nombre_contacto(nvl(a.inspector,0)),'- inspeccion web -') nom_CONTACTO_ins,  " +
                " a.sumaseg, a.sumaseg_autorizada, case a.km_millas when 'K' then 'km' else 'mi' end km_millas" +
                " ,case a.agenciavh when 'S' then 'Agencia' else 'Importado' end agenciavh" +
                " FROM " + " tinspeccvh a, mg_contactos b " + " WHERE " + " a.cod_contacto=b.cod_contacto(+) and " +
                " a.INSPECCION = ? ";

            PreparedStatement st = portal.getConnection().prepareStatement(consulta);
            st.setObject(1, request.getParameter("ID_INSPECCION"));

            rs = st.executeQuery();

            if (rs.next()) {

                nombre = rs.getString("nombre_contacto");
                cod_contacto = rs.getString("COD_CONTACTO");
                nit = rs.getString("NIT");
                telefono = rs.getString("TELEFONO");
                cvemarca = rs.getString("CVEMARCA");
                cveestil = rs.getString("CVEESTIL");
                cveuso = rs.getString("CVEUSO");
                cvecolor = rs.getString("CVECOLOR");
                cvetvehi = rs.getString("CVETVEHI");
                anio = rs.getString("ANIO");
                placas = (rs.getString("PLACAS") != null) ? rs.getString("PLACAS").toUpperCase() : "";
                kilometros = rs.getString("KILOMETROS");
                chasis = rs.getString("CHASIS");
                motor = rs.getString("MOTOR");
                transmision = rs.getString("TRANSMISION");
                cod_contacto2 = rs.getString("COD_CONTACTO2");
                cveage = rs.getString("CVEAGE");
                sumaseg = rs.getString("SUMASEG");
                km_mi= rs.getString("KM_MILLAS");
                procedencia = rs.getString("AGENCIAVH");
                sumasegAuth = rs.getString("SUMASEG_AUTORIZADA");
                id_inspeccion = request.getParameter("ID_INSPECCION").trim();
                OBSERVACIONES = rs.getString("OBSERVACIONES");
                nombre_inspector = rs.getString("nom_CONTACTO_ins");

            }

            consulta = "select desmarca, desestil, t.destvehi, c.descolor\n" +
                    "  from tmarcas m, testilos e\n" +
                    "        , ttipovehi t, tcolores c\n" +
                    " where m.cvemarca = e.cvemarca\n" +
                    "    and t.cvetvehi = ?\n" +
                    "    and c.cvecolor = ?\n" +
                    "    and m.cvemarca = ?\n" +
                    "    and e.cveestil = ?";

            st = portal.getConnection().prepareStatement(consulta);

            st.setObject(1, cvetvehi);
            st.setObject(2, cvecolor);
            st.setObject(3, cvemarca);
            st.setObject(4, cveestil);

            rs = st.executeQuery();

            if (rs.next()) {
                str_marca = rs.getString(1);
                str_estilo = rs.getString(2);
                str_tipovehiculo = rs.getString(3);
                str_color = rs.getString(4);
            }

            consulta =
                    "select nvl(telefono1,' ' ), nvl(direccion_string,' ') from mg_direcciones where cod_contacto = ? " +
                    " and cod_tipo_direccion in (1,3) order by cod_tipo_direccion desc ";

            st = portal.getConnection().prepareStatement(consulta);

            st.setObject(1, cod_contacto2);

            rs = st.executeQuery();

            if (rs.next()) {
                str_direccion = rs.getString(2);
                telefono = rs.getString(1);
            }

            cadena = "select to_char(fchinspeccion,'dd/mm/rrrr') from tinspeccvh where inspeccion = ?";

            st = portal.getConnection().prepareStatement(cadena);

            st.setObject(1, id_inspeccion);

            rs = st.executeQuery();

            if (rs.next())
                str_fecha = rs.getString(1);

            float cel_tam_datos[] = { 50, 20, 20 };
            doc_pdf.addTitle("Inspección de Bien");
            doc_pdf.addSubject("Seguros Universales S.A");
            doc_pdf.addAuthor("IT");
            doc_pdf.addCreator("IT");

            PdfWriter writer = PdfWriter.getInstance(doc_pdf, out);
            //writer.setEncryption(null, clave.getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.STANDARD_ENCRYPTION_128);

            doc_pdf.open();
            
            PdfContentByte canvas = writer.getDirectContentUnder();
            Image image = Image.getInstance("cartaVertical.jpg");
            image.scalePercent(48);
            image.setAbsolutePosition(0, 0);
            canvas.addImage(image);

                
            PdfPTable tabla_encabezado = new PdfPTable(2);
            tabla_encabezado.setWidths(new int[] { 80, 20 });
            /*tabla_encabezado.setBorder(0);
            tabla_encabezado.setBorderWidth(0);
            tabla_encabezado.setPadding(0);
            tabla_encabezado.setSpacing(0);*/            
            
            System.out.println("á ´b c é í Ö Ó");

            Paragraph parra =
                new Paragraph("Ficha de Inspección de vehiculo dós", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD));
       
            PdfPCell celda_encabezado = new PdfPCell(parra);
            celda_encabezado.setBorder(Rectangle.NO_BORDER);
            tabla_encabezado.addCell(celda_encabezado);
            
            celda_encabezado = new PdfPCell();
            celda_encabezado.setBorder(Rectangle.NO_BORDER);
            tabla_encabezado.addCell(celda_encabezado);
                       
           
            parra = new Paragraph("P-RC-15-R-01-V1", FontFactory.getFont(FontFactory.HELVETICA, 8));
            //parra.setLeading(8);
            celda_encabezado = new PdfPCell(parra);
            celda_encabezado.setBorder(Rectangle.NO_BORDER);
            tabla_encabezado.addCell(celda_encabezado);            
            
            parra = new Paragraph("Inspección Nó " + id_inspeccion, FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD));
            //parra.setLeading(8);
            celda_encabezado = new PdfPCell(parra);            
            celda_encabezado.setBorder(Rectangle.NO_BORDER);
            
            tabla_encabezado.addCell(celda_encabezado);
            doc_pdf.add(tabla_encabezado);

            //////////////////////////////    TABLA DATOS DEL ASEGURADO        ////////////////////
            Table tabla_datos = new Table(3);
            tabla_datos.setWidths(cel_tam_datos);
            tabla_datos.setBorder(1);
            tabla_datos.setPadding(0);
            tabla_datos.setSpacing(2);


            parra = new Paragraph("Datos del Asegurado", FontFactory.getFont(FontFactory.HELVETICA, 7, Font.BOLD));
            parra.setLeading(7);
            Cell celda = new Cell(parra);
            celda.setBorderColor(new Color(255, 255, 255));            
            celda.setHorizontalAlignment(celda.ALIGN_LEFT);
            celda.setHeader(true);
            celda.setColspan(3);
            tabla_datos.addCell(celda);


            parra = new Paragraph("Nombre o Razón Social", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celda = new Cell(parra);
            celda.setColspan(2);
            celda.setBorderColor(new Color(255, 255, 255));
            tabla_datos.addCell(celda);

            parra = new Paragraph("NIT", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celda = new Cell(parra);
            celda.setBorderColor(new Color(255, 255, 255));
            tabla_datos.addCell(celda);

            parra = new Paragraph(" " + nombre, FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(8);
            celda = new Cell(parra);
            celda.setColspan(2);
            tabla_datos.addCell(celda);

            parra = new Paragraph(" " + nit, FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(8);
            celda = new Cell(parra);
            tabla_datos.addCell(celda);

            parra = new Paragraph("Dirección de Cobro", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celda = new Cell(parra);
            celda.setColspan(2);
            celda.setBorderColor(new Color(255, 255, 255));
            tabla_datos.addCell(celda);

            parra = new Paragraph("Código de Contacto", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celda = new Cell(parra);
            celda.setHorizontalAlignment(celda.ALIGN_BOTTOM);
            celda.setBorderColor(new Color(255, 255, 255));
            tabla_datos.addCell(celda);

            parra = new Paragraph(" " + str_direccion, FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(8);
            celda = new Cell(parra);
            celda.setColspan(2);
            //        celda.setLeading(7);
            tabla_datos.addCell(celda);

            parra = new Paragraph(" " + cod_contacto2, FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(8);
            celda = new Cell(parra);
            tabla_datos.addCell(celda);
            doc_pdf.add(tabla_datos);
            //////////////////////////////    TABLA DATOS DEL VEHICULO
            Table tabla_vehiculo = new Table(7);
            tabla_vehiculo.setBorder(1);
            tabla_vehiculo.setPadding(0);
            tabla_vehiculo.setSpacing(2);

            parra = new Paragraph("Datos del Vehículo ", FontFactory.getFont(FontFactory.HELVETICA, 7, Font.BOLD));
            parra.setLeading(7);
            celda = new Cell(parra);
            celda.setBorderColor(new Color(255, 255, 255));            
            celda.setHorizontalAlignment(celda.ALIGN_LEFT);
            celda.setHeader(true);
            celda.setColspan(7);
            tabla_vehiculo.addCell(celda);

            parra = new Paragraph("Marca:", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celda = new Cell(parra);
            celda.setBorderColor(new Color(255, 255, 255));
            tabla_vehiculo.addCell(celda);

            parra = new Paragraph("Estilo:", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celda = new Cell(parra);
            celda.setBorderColor(new Color(255, 255, 255));
            tabla_vehiculo.addCell(celda);

            parra = new Paragraph("Año:", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celda = new Cell(parra);
            celda.setBorderColor(new Color(255, 255, 255));
            tabla_vehiculo.addCell(celda);

            parra = new Paragraph("Nó. Placas:", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celda = new Cell(parra);
            celda.setBorderColor(new Color(255, 255, 255));
            tabla_vehiculo.addCell(celda);

            parra = new Paragraph("Km / Millas:", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celda = new Cell(parra);
            celda.setBorderColor(new Color(255, 255, 255));
            tabla_vehiculo.addCell(celda);

            parra = new Paragraph("Tipo:", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celda = new Cell(parra);
            celda.setBorderColor(new Color(255, 255, 255));
            tabla_vehiculo.addCell(celda);

            parra = new Paragraph("Color:", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celda = new Cell(parra);
            celda.setBorderColor(new Color(255, 255, 255));
            tabla_vehiculo.addCell(celda);

            parra = new Paragraph(" " + str_marca, FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(8);
            celda = new Cell(parra); //marca
            tabla_vehiculo.addCell(celda);

            parra = new Paragraph(" " + str_estilo, FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(8);
            celda = new Cell(parra); //estilo
            tabla_vehiculo.addCell(celda);

            parra = new Paragraph(" " + anio, FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(8);
            celda = new Cell(parra); //anio
            tabla_vehiculo.addCell(celda);

            parra = new Paragraph(" " + placas, FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(8);
            celda = new Cell(parra); //placas
            tabla_vehiculo.addCell(celda);

            parra = new Paragraph(" " + kilometros + " " + km_mi, FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(8);
            celda = new Cell(parra); //km/millas
            tabla_vehiculo.addCell(celda);

            parra = new Paragraph(" " + str_tipovehiculo, FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(8);
            celda = new Cell(parra); //tipo
            tabla_vehiculo.addCell(celda);

            parra = new Paragraph(" " + str_color, FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(8);
            celda = new Cell(parra); //color
            tabla_vehiculo.addCell(celda);


            doc_pdf.add(tabla_vehiculo);

            Table tabla_vehiculo2 = new Table(4);
            tabla_vehiculo2.setBorder(0);
            tabla_vehiculo2.setPadding(0);
            tabla_vehiculo2.setSpacing(2);
            int colums[] = { 30, 30, 20, 20 };

            tabla_vehiculo2.setWidths(colums);

            parra = new Paragraph("Nó. Chasis:", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celda = new Cell(parra);
            celda.setBorderColor(new Color(255, 255, 255));
            tabla_vehiculo2.addCell(celda);

            parra = new Paragraph("Nó. Motor: ", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celda = new Cell(parra);
            celda.setBorderColor(new Color(255, 255, 255));
            tabla_vehiculo2.addCell(celda);

            parra = new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celda = new Cell(parra);
            celda.setBorderColor(new Color(255, 255, 255));
            tabla_vehiculo2.addCell(celda);

            parra = new Paragraph("Cambio:", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celda = new Cell(parra);
            celda.setBorderColor(new Color(255, 255, 255));
            tabla_vehiculo2.addCell(celda);

            parra = new Paragraph(" " + chasis, FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(8);
            celda = new Cell(parra); //chasis
            tabla_vehiculo2.addCell(celda);

            parra = new Paragraph(" " + motor, FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(8);
            celda = new Cell(parra); //motor
            tabla_vehiculo2.addCell(celda);

            parra = new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(8);
            celda = new Cell(parra);
            //-------------------------------------------------------
            celda.setHorizontalAlignment(celda.ALIGN_CENTER);
            celda.setVerticalAlignment(celda.ALIGN_CENTER);
            tabla_vehiculo2.addCell(celda);


            if (transmision.compareTo("1") == 0) {
                parra = new Paragraph("[X]Man.  [ ]Aut.", FontFactory.getFont(FontFactory.HELVETICA, 7));
                parra.setLeading(8);
                celda = new Cell(parra); //transmision
            } else if (transmision.compareTo("2") == 0) {
                parra = new Paragraph("[ ]Man.  [X]Aut.", FontFactory.getFont(FontFactory.HELVETICA, 7));
                parra.setLeading(8);
                celda = new Cell(parra); //transmision
            } else {
                parra = new Paragraph("[ ]Man.  [ ]Aut.", FontFactory.getFont(FontFactory.HELVETICA, 7));
                parra.setLeading(8);
                celda = new Cell(parra); //transmision
            }
            
            celda.setHorizontalAlignment(celda.ALIGN_CENTER);
            celda.setVerticalAlignment(celda.ALIGN_CENTER);
            tabla_vehiculo2.addCell(celda);
            doc_pdf.add(tabla_vehiculo2);
            
            Table tabla_vehiculo3 = new Table(4);
            tabla_vehiculo3.setBorder(0);
            tabla_vehiculo3.setPadding(0);
            tabla_vehiculo3.setSpacing(2);
            int colums2[] = { 30, 30, 20, 20 };
    
            tabla_vehiculo3.setWidths(colums2);
            
            parra = new Paragraph("Procedencia:", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celda = new Cell(parra);
            celda.setBorderColor(new Color(255, 255, 255));
            tabla_vehiculo3.addCell(celda);

            parra = new Paragraph("Suma Asegurada Referida:", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celda = new Cell(parra);
            celda.setBorderColor(new Color(255, 255, 255));
            tabla_vehiculo3.addCell(celda);

            parra = new Paragraph("Suma Asegurada Autorizada:", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celda = new Cell(parra);
            celda.setBorderColor(new Color(255, 255, 255));
            tabla_vehiculo3.addCell(celda);
            
            parra = new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celda = new Cell(parra);
            celda.setBorderColor(new Color(255, 255, 255));
            tabla_vehiculo3.addCell(celda);
            
            parra = new Paragraph(" " + procedencia, FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(8);
            celda = new Cell(parra); //procedencia
            tabla_vehiculo3.addCell(celda);

            parra = new Paragraph(" " + sumaseg, FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(8);
            celda = new Cell(parra); //suma asegurada referida
            tabla_vehiculo3.addCell(celda);
            
            parra = new Paragraph(" " + sumasegAuth, FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(8);
            celda = new Cell(parra); //suma asegurada autorizada
            tabla_vehiculo3.addCell(celda);
            
            parra = new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(8);
            celda = new Cell(parra);
            
            celda.setHorizontalAlignment(celda.ALIGN_CENTER);
            celda.setVerticalAlignment(celda.ALIGN_CENTER);
            tabla_vehiculo3.addCell(celda);
            doc_pdf.add(tabla_vehiculo3);


            consulta = " select count(*) " + " from tinspvhdet  " + " where inspeccion= ? ";

            st = portal.getConnection().prepareStatement(consulta);
            st.setObject(1, id_inspeccion);

            rs = st.executeQuery();

            int filas = 0;
            if (rs.next()) {
                filas = rs.getInt(1) + 1;
            }


            Table tabla = new Table(11);
            tabla.setBorder(1);
            tabla.setPadding(1);
            tabla.setSpacing(0);
            tabla.setWidths(new float[] { 4, 30, 4, 4, 2, 4, 4, 2, 40, 25, 25 });

            String texto = "";

            Paragraph parrafito = new Paragraph(texto, FontFactory.getFont(FontFactory.HELVETICA, 5));
            parrafito.setLeading(5);


            celda = new Cell();
            parrafito = new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 5));
            parrafito.setLeading(5);
            celda.add(parrafito);
            celda.setColspan(2);
            tabla.addCell(celda);


            celda = new Cell();
            celda.setColspan(2);
            celda.setBorder(0);
            celda.setHorizontalAlignment(celda.ALIGN_CENTER);
            parrafito = new Paragraph("EXISTE", FontFactory.getFont(FontFactory.HELVETICA, 5));
            parrafito.setLeading(5);
            celda.setBorderColor(new Color(0, 0, 0));
            celda.add(parrafito);
            tabla.addCell(celda);

            celda = new Cell();
            parrafito = new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 5));
            parrafito.setLeading(5);
            celda.setBorderColor(new Color(0, 0, 0));
            celda.setBorder(Rectangle.LEFT);
            celda.add(parrafito);
            tabla.addCell(celda);

            celda = new Cell();
            celda.setColspan(2);
            celda.setHorizontalAlignment(celda.ALIGN_CENTER);
            parrafito = new Paragraph("DA�ADA", FontFactory.getFont(FontFactory.HELVETICA, 5));
            celda.setBorderColor(new Color(0, 0, 0));
            parrafito.setLeading(5);
            celda.add(parrafito);
            tabla.addCell(celda);

            celda = new Cell();
            celda.setColspan(2);
            celda.setHorizontalAlignment(celda.ALIGN_CENTER);
            parrafito = new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 5));
            parrafito.setLeading(5);
            celda.setBorderColor(new Color(0, 0, 0));
            celda.setBorder(Rectangle.LEFT);
            celda.add(parrafito);
            tabla.addCell(celda);

            Image foto = null;
            celda = new Cell();
            celda.setBorderColor(new Color(255, 255, 255));
            parra = null;

            celda.setRowspan(filas + 2);


            for (int i = 1; i < 13; i = i + 2) {
                String is = "";
                if (i > 9)
                    is = i + "";
                else
                    is = "0" + i;

                foto = null;
                try {

                    String sfoto = "/mnt/fotosInspecciones/" + placas.toUpperCase().trim() + " 0" + is + ".jpg";


                    foto = Image.getInstance(sfoto);
                    foto.scaleAbsolute(70, 50);
                    foto.setAlignment(Image.ALIGN_CENTER);
                    celda.add(new Paragraph("\n\n"));
                    celda.add(foto);

                } catch (Exception e) {

                    try {

                        String sfoto = "/mnt/inspecciones/" + placas.toUpperCase().trim() + " 0" + is + ".gif";

                        foto = Image.getInstance(sfoto);
                        foto.scaleAbsolute(70, 50);
                        foto.setAlignment(Image.ALIGN_CENTER);
                        celda.add(new Paragraph("\n\n"));
                        celda.add(foto);

                    } catch (Exception e2) {
                        parra = new Paragraph("", FontFactory.getFont(FontFactory.HELVETICA, 4));
                        parra.setLeading(4);
                        celda.add(parra);
                    }

                }

            }
            tabla.addCell(celda);


            foto = null;
            celda = new Cell();
            parra = null;
            celda.setRowspan(filas + 2);
            celda.setBorderColor(new Color(255, 255, 255));
            for (int i = 2; i < 13; i = i + 2) {
                String is = "";
                if (i > 9)
                    is = i + "";
                else
                    is = "0" + i;


                foto = null;
                try {

                    String sfoto = "/mnt/fotosInspecciones/" + placas.toUpperCase().trim() + " 0" + is + ".jpg";

                    foto = Image.getInstance(sfoto);
                    foto.scaleAbsolute(70, 50);
                    foto.setAlignment(Image.ALIGN_CENTER);
                    celda.add(new Paragraph("\n\n"));
                    celda.add(foto);

                } catch (Exception e) {

                    try {

                        String sfoto = "/mnt/inspecciones/" + placas.toUpperCase().trim() + " 0" + is + ".gif";

                        foto = Image.getInstance(sfoto);
                        foto.scaleAbsolute(70, 50);
                        foto.setAlignment(Image.ALIGN_CENTER);
                        celda.add(new Paragraph("\n\n"));
                        celda.add(foto);

                    } catch (Exception e2) {
                        parra = new Paragraph("", FontFactory.getFont(FontFactory.HELVETICA, 4));
                        parra.setLeading(4);
                        celda.add(parra);
                    }

                }

            }
            tabla.addCell(celda);


            celda = new Cell();
            parrafito = new Paragraph("#", FontFactory.getFont(FontFactory.HELVETICA, 5));
            parrafito.setLeading(5);
            celda.setHorizontalAlignment(celda.ALIGN_CENTER);
            celda.add(parrafito);
            tabla.addCell(celda);

            celda = new Cell();
            parrafito = new Paragraph("PARTE", FontFactory.getFont(FontFactory.HELVETICA, 5));
            parrafito.setLeading(5);
            celda.setHorizontalAlignment(celda.ALIGN_CENTER);
            celda.add(parrafito);
            tabla.addCell(celda);

            celda = new Cell();
            parrafito = new Paragraph("SI", FontFactory.getFont(FontFactory.HELVETICA, 5));
            parrafito.setLeading(5);
            celda.setHorizontalAlignment(celda.ALIGN_CENTER);
            celda.add(parrafito);
            tabla.addCell(celda);

            celda = new Cell();
            parrafito = new Paragraph("NO", FontFactory.getFont(FontFactory.HELVETICA, 5));
            parrafito.setLeading(5);
            celda.setHorizontalAlignment(celda.ALIGN_CENTER);
            celda.add(parrafito);
            tabla.addCell(celda);

            celda = new Cell();
            parrafito = new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 5));
            parrafito.setLeading(5);
            celda.setBorderColor(new Color(255, 255, 255));
            celda.add(parrafito);
            tabla.addCell(parrafito);

            celda = new Cell();
            parrafito = new Paragraph("SI", FontFactory.getFont(FontFactory.HELVETICA, 5));
            parrafito.setLeading(5);
            celda.setHorizontalAlignment(celda.ALIGN_CENTER);
            celda.add(parrafito);
            tabla.addCell(celda);

            celda = new Cell();
            parrafito = new Paragraph("NO", FontFactory.getFont(FontFactory.HELVETICA, 5));
            parrafito.setLeading(5);
            celda.setHorizontalAlignment(celda.ALIGN_CENTER);
            celda.add(parrafito);
            tabla.addCell(celda);

            celda = new Cell();
            parrafito = new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 5));
            parrafito.setLeading(5);
            celda.setBorderColor(new Color(0, 0, 0));
            celda.setBorder(Rectangle.LEFT);
            celda.add(parrafito);
            tabla.addCell(parrafito);

            celda = new Cell();
            parrafito = new Paragraph(" OBSERVACIONES ", FontFactory.getFont(FontFactory.HELVETICA, 5));
            parrafito.setLeading(5);
            celda.setBorderColor(new Color(0, 0, 0));
            celda.setHorizontalAlignment(celda.ALIGN_CENTER);
            celda.add(parrafito);
            tabla.addCell(celda);
            ///////        LLENADO DE INFORMACION


            consulta =
                    "select a.inspeccion, b.descripcion, a.existe, a.danio, a.observaciones " + " from tinspvhdet a, tpartesvh b " +
                    " where a.inspeccion= ? and a.parte=b.parte";

            st = portal.getConnection().prepareStatement(consulta);
            st.setObject(1, id_inspeccion);

            rs = st.executeQuery();

            int cont = 0;
            while (rs.next()) {
                cont++;

                celda = new Cell();
                parrafito = new Paragraph(cont + "", FontFactory.getFont(FontFactory.HELVETICA, 4));
                parrafito.setLeading(4);
                celda.setHorizontalAlignment(celda.ALIGN_RIGHT);
                celda.add(parrafito);
                tabla.addCell(celda);


                celda = new Cell();
                parrafito =
                        new Paragraph((rs.getString("descripcion") != null ? rs.getString("descripcion") : ""), FontFactory.getFont(FontFactory.HELVETICA,
                                                                                                                                    4));
                parrafito.setLeading(4);
                celda.add(parrafito);
                tabla.addCell(celda);

                if (rs.getString("existe").equals("S")) {
                    celda = new Cell();
                    parrafito = new Paragraph("X", FontFactory.getFont(FontFactory.HELVETICA, 4));
                    parrafito.setLeading(4);
                    celda.setHorizontalAlignment(celda.ALIGN_CENTER);
                    celda.setBorderColor(new Color(0, 0, 0));
                    celda.add(parrafito);
                    tabla.addCell(celda);

                    celda = new Cell();
                    parrafito = new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 4));
                    parrafito.setLeading(4);
                    celda.setHorizontalAlignment(celda.ALIGN_CENTER);
                    celda.add(parrafito);
                    tabla.addCell(celda);
                } else {
                    celda = new Cell();
                    parrafito = new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 4));
                    parrafito.setLeading(4);
                    celda.setHorizontalAlignment(celda.ALIGN_CENTER);
                    celda.add(parrafito);
                    tabla.addCell(celda);

                    celda = new Cell();
                    parrafito = new Paragraph("X", FontFactory.getFont(FontFactory.HELVETICA, 4));
                    parrafito.setLeading(4);
                    celda.setHorizontalAlignment(celda.ALIGN_CENTER);
                    celda.add(parrafito);
                    tabla.addCell(celda);
                }

                celda = new Cell();
                parrafito = new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 4));
                parrafito.setLeading(4);
                celda.setBorderColor(new Color(0, 0, 0));
                celda.setBorder(Rectangle.LEFT);
                celda.add(parrafito);
                tabla.addCell(celda);

                if (rs.getString("danio").equals("S")) {
                    celda = new Cell();
                    parrafito = new Paragraph("X", FontFactory.getFont(FontFactory.HELVETICA, 4));
                    parrafito.setLeading(4);
                    celda.setHorizontalAlignment(celda.ALIGN_CENTER);
                    celda.add(parrafito);
                    tabla.addCell(celda);

                    celda = new Cell();
                    parrafito = new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 4));
                    parrafito.setLeading(4);
                    celda.setHorizontalAlignment(celda.ALIGN_CENTER);
                    celda.add(parrafito);
                    tabla.addCell(celda);
                } else {
                    celda = new Cell();
                    parrafito = new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 4));
                    parrafito.setLeading(4);
                    celda.setHorizontalAlignment(celda.ALIGN_CENTER);
                    celda.add(parrafito);
                    tabla.addCell(celda);

                    celda = new Cell();
                    parrafito = new Paragraph("X", FontFactory.getFont(FontFactory.HELVETICA, 4));
                    parrafito.setLeading(4);
                    celda.setHorizontalAlignment(celda.ALIGN_CENTER);
                    celda.add(parrafito);
                    tabla.addCell(celda);
                }

                celda = new Cell();
                parrafito = new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 4));
                parrafito.setLeading(4);
                celda.setBorderColor(new Color(0, 0, 0));
                celda.setBorder(Rectangle.LEFT);
                celda.add(parrafito);
                tabla.addCell(celda);

                celda = new Cell();
                parrafito =
                        new Paragraph((rs.getString("observaciones") != null ? rs.getString("observaciones") : "-"),
                                      FontFactory.getFont(FontFactory.HELVETICA, 4));
                parrafito.setLeading(4);
                celda.setHorizontalAlignment(celda.ALIGN_LEFT);
                celda.setBorderColor(new Color(0, 0, 0));
                celda.add(parrafito);
                tabla.addCell(celda);


            }

            celda = new Cell();
            parrafito = new Paragraph(OBSERVACIONES, FontFactory.getFont(FontFactory.HELVETICA, 7));
            parrafito.setLeading(7);
            celda.setHorizontalAlignment(celda.ALIGN_JUSTIFIED);
            celda.setBorderColor(new Color(0, 0, 0));
            celda.setColspan(9);
            celda.add(parrafito);
            tabla.addCell(celda);            

            doc_pdf.add(tabla);


            //////////////////////////////    TABLA PIE DE PAGINA Y Nota
            PdfPTable tabla_pie = new PdfPTable(2);
            int cols_pie[] = { 30, 70 };
            /*tabla_pie.setBorder(1);
            tabla_pie.setPadding(0);
            tabla_pie.setSpacing(2);*/
            tabla_pie.setWidths(cols_pie);
            //tabla_pie.setAutoFillEmptyCells(true);

            String cadena2 = "Nota: Los aros de Magnesio solo se cubren en la cobertura de robo total y siempre y cuando sean originales de fabrica. No se incluyen la cobertura de robo parcial. "
                           + " Para asegurar el vehículo deben adjuntarse a esta ficha una copia de la tarjeta de circulacion y la solicitud del seguro."
                           + " Asi mismo el asegurado declara estar de acuerdo con todo lo especificado en esta ficha. La reparacion de los daños anotados"
                           + " anteriormente no estarán a cargo de la Aseguradora aun en caso de accidente.";
            
            Paragraph parrafo = new Paragraph(cadena2, FontFactory.getFont(FontFactory.HELVETICA, 6));
            parrafo.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            
            PdfPCell celdaf = new PdfPCell();                        
            celdaf.addElement(parrafo);            
            
            parrafo = new Paragraph("El presente documento NO es un certificado de seguro, tampoco implica que el vehículo ha sido aceptado como riesgo. El Inspector NO está autorizado a asegurar vehículos ni a tomar decisiones sobre el seguro, para más información comuniquese con su corredor o Suscripción de Universales.",FontFactory.getFont(FontFactory.HELVETICA, 6, Font.BOLD));
            parrafo.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            celdaf.addElement(parrafo);
            
            celdaf.setBorder(Rectangle.NO_BORDER);
                        
            tabla_pie.addCell(celdaf);
            PdfPTable tabla_dentro = new PdfPTable(4);            
            int tam_cols[] = { 40, 20, 30, 30 };
            tabla_dentro.setWidths(tam_cols);            

            /*tabla_dentro.setBorder(1);
            tabla_dentro.setPadding(0);
            tabla_dentro.setSpacing(1);*/

            parra = new Paragraph("Inspector:", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celdaf = new PdfPCell(parra);
            celdaf.setColspan(2);
            celdaf.setBorder(Rectangle.NO_BORDER);
            tabla_dentro.addCell(celdaf);

            parra = new Paragraph("Nó de Agente", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celdaf = new PdfPCell(parra);
            celdaf.setBorder(Rectangle.NO_BORDER);
            tabla_dentro.addCell(celdaf);

            parra = new Paragraph("Fecha:", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celdaf = new PdfPCell(parra);
            celdaf.setBorder(Rectangle.NO_BORDER);
            tabla_dentro.addCell(celdaf);

            parra =
new Paragraph(" (" + cod_contacto + ") " + nombre_inspector, FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celdaf = new PdfPCell(parra); //CODIGO INSPECTOR
                        
            celdaf.setColspan(2);
            tabla_dentro.addCell(celdaf);

            parra = new Paragraph(cveage, FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celdaf = new PdfPCell(parra); // NUMERO DE AGENTE            
            celdaf.setHorizontalAlignment(celdaf.ALIGN_RIGHT);
            tabla_dentro.addCell(celdaf);

            parra = new Paragraph(str_fecha, FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celdaf = new PdfPCell(parra); //FECHA DE REALIZACION DE LA FICHA            
            celdaf.setHorizontalAlignment(celdaf.ALIGN_RIGHT);
            tabla_dentro.addCell(celdaf);

            parra = new Paragraph("Firma Asegurado:", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celdaf = new PdfPCell(parra);            
            celdaf.setColspan(4);            
            celdaf.setBorder(Rectangle.NO_BORDER);
            tabla_dentro.addCell(celdaf);

            /*parra = new Paragraph("Firma Asegurado:", FontFactory.getFont(FontFactory.HELVETICA, 7));
            parra.setLeading(7);
            celda = new Cell(parra);
            celda.setColspan(2);
            celda.setBorderColor(new Color(255, 255, 255));            
            tabla_dentro.addCell(celda);*/
            
            /*celda = new Cell(new Chunk(" ", FontFactory.getFont(FontFactory.HELVETICA, 7)));
            celda.setColspan(2);
            celda.setRowspan(2);
            //celda.setBorderColor(new Color(255, 255, 255));            
            tabla_dentro.addCell(celda);*/

            celdaf = new PdfPCell(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 7)));            
            celdaf.setColspan(4);            
            
            try {

                String sfoto = "/mnt/inspecciones/" + placas.toUpperCase().trim() + "_FIRMA.png";

                foto = Image.getInstance(sfoto);                
                
                foto.scalePercent(40);
                celdaf.addElement(foto);
                
              //  celdaf.setBorder(Rectangle.NO_BORDER);

            } catch (Exception e2) {                
                try{
                    String sfoto = "/mnt/inspecciones/" + placas.toUpperCase().trim() + "_FIRMA.gif";
    
                    foto = Image.getInstance(sfoto);                
                    
                    foto.scalePercent(40);
                    celdaf.addElement(foto);                    
            //        celdaf.setBorder(Rectangle.NO_BORDER);
                }catch(Exception e3){
                }
            }
            tabla_dentro.addCell(celdaf); 
            
            celdaf = new PdfPCell(tabla_dentro);
            celdaf.setBorder(Rectangle.NO_BORDER);

            tabla_pie.addCell(celdaf);
            doc_pdf.add(tabla_pie);
            
            doc_pdf.close();
            //}
        } catch (Exception error) {
            error.printStackTrace();
            SendError.html(error.getMessage(), getClass().getName(),
                           ((Usuario)request.getSession().getAttribute("usuario")).getUsuario(),
                           error.toString() + "<br>" + portal.getStackTrace(error), "");


        } finally {
            portal.close();
        }

        
        response.getOutputStream().write(out.toByteArray());

    }
}
