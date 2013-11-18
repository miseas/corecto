<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form2" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Co-Recto BD</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <!-- Le styles -->
    <link href="css/bootstrap.css" rel="stylesheet">
    <style type="text/css">
			body {
			    padding-bottom: 10px;
			    padding-top: 60px;
			}
	      .sidebar-nav {
	        padding: 9px 0;
	      }
	
	      @media (max-width: 980px) {
	        /* Enable use of floated navbar text */
	        .navbar-text.pull-right {
	          float: none;
	          padding-left: 5px;
	          padding-right: 5px;
	        }
	      }
	      
		#footer {
		background-color: #f5f5f5;
		}
		/* Lastly, apply responsive CSS fixes as necessary */
		@media (max-width: 767px) {
		#footer {
		margin-left: -20px;
		margin-right: -20px;
		padding-left: 20px;
		padding-right: 20px;
		}
		} 
		
		.show-grid[class*="span"] {
			background-color: #F5F5F5;
		    border: 1px solid #E3E3E3;
		    border-radius: 4px 4px 4px 4px;
		    box-shadow: 0 1px 1px rgba(0, 0, 0, 0.05) inset;
		    min-height: 20px;
		    padding: 10px 19px;
		}
    </style>
    
    <link href="css/bootstrap-responsive.css" rel="stylesheet">
	<link href="css/font-awesome.min.css" rel="stylesheet">

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="../assets/js/html5shiv.js"></script>
    <![endif]-->

    <!-- Fav and touch icons -->
    <link rel="apple-touch-icon-precomposed" sizes="144x144" href="../assets/ico/apple-touch-icon-144-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="114x114" href="../assets/ico/apple-touch-icon-114-precomposed.png">
     <link rel="apple-touch-icon-precomposed" sizes="72x72" href="../assets/ico/apple-touch-icon-72-precomposed.png">
     <link rel="apple-touch-icon-precomposed" href="../assets/ico/apple-touch-icon-57-precomposed.png">
     <link rel="shortcut icon" href="../assets/ico/favicon.png">
                  
  </head>

  <body>

		<jsp:include flush="true" page="menu.jsp">
				<jsp:param name="left" value="1" />
		</jsp:include>

    <div class="container">
        <div class="hero-unit">
      		<sec:authorize ifNotGranted="ROLE_USER,ROLE_ADMIN">        
	            <h1>Logueo de usuario</h1>
	            <p>Se deberá loguear para poder acceder a las diferentes opciones del menú</p>
	            <p><a href="#" id="helpLoginButton" class="btn btn-primary btn-large">Ayuda &raquo;</a></p>
            </sec:authorize>
            <sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">          
	            	<h1>Bienvenido!</h1>
	            	<div class="alert alert-success">
	                <p>Se encuentra logueado con el usuario:
	                		 <sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">
							 <strong><sec:authentication property="principal.username"/></strong>
							 </sec:authorize>
						. Si lo desea puede <span class="label label-info" style="font-size: 14px">Salir</span> y loguearse con otro usuario</p>
					</div>	
	                
	               	<c:url value="/logout" var="logoutUrl"/>	  
	          	 <p><a class="btn btn-primary btn-large" href="${logoutUrl}" style="font-size: 20px;" >Salir <i style="margin-left:5px" class="icon-share"></i></a></p>                 
	         </sec:authorize>
          </div>
      <div class="row-fluid">
            <div class="span3 show-grid">
              <h2>Ultimos clientes agregados</h2>
              <p>Puede mostrarse info acerca de los ultimos cientes agregados, sus estados, incidencias, algun otro dato de rapido acceso</p>
            </div><!--/span-->
            <div class="span3 show-grid">
              <h2>Otra info</h2>
              <p>Cualquier otra informacion que valga la pena mostrar en la pantalla principal y sin necesitar logueo...o si</p>
              <p><a class="btn" href="#">Mas detalles &raquo;</a></p>
            </div><!--/span-->
          </div><!--/row-->

      <hr>

    </div><!--/.fluid-container-->
  <div id="footer">
      <div class="container">
        <p class="muted credit">Creada por <a href="#">[Matias Iseas]</a>.</p>
      </div>
    </div>
    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script type="text/javascript" src="js/jquery-1.7.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
<script type="text/javascript">
jQuery(function() {

<sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">
	jQuery("div[lang='loginDivData']").hide();
	jQuery("div[lang='alreadyLoggued']").show();
</sec:authorize>
<sec:authorize ifNotGranted="ROLE_USER">
jQuery("div[lang='loginDivData']").show();
jQuery("#j_username").focus();
</sec:authorize>

$('#helpLoginButton').popover({html:true,content:"Debe acceder con un <strong>usuario/password</strong> en la barra de menú de arriba"});


});
</script> 

  </body>
</html>