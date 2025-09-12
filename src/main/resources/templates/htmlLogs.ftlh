<!DOCTYPE html>
<html>
	<head>
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
		<style>
			body {background-color: white;}
			.headers {color: #0000ff; background-color: #b4b4b4; font-weight: bold;}
			table {
  				table-layout: auto;
  				border: 0;
  				border-collapse: collapse;
			}
			tr {
				border-bottom: 1pt solid black;
			}
			tr:last-child {
				border-bottom: 0;
			}
			td {
				border-right: 1pt solid #CCCCCC;
				min-width: 140px;
			}
			td:last-child {
			 	border-right: 0;
				min-width: 140px;
			}
			.menu {
				float: right;
				flex-direction: row;
				min-width: 55px;
			}
			button {
				float: right;
				margin: 2px;
				border-radius: 25%;
				background-color: #b4b4b4;
				color: white;
				padding: 2px;
				font-size: 16px;
				border: none;
				cursor: pointer;
			}
			button:disabled {
				cursor: not-allowed;
				pointer-events: all !important;
				background-color: #CCCCCC;
			}

			/* The Modal (background) */
			.modal {
				display: none; /* Hidden by default */
				position: fixed; /* Stay in place */
				z-index: 1; /* Sit on top */
				padding-top: 100px; /* Location of the box */
				left: 0;
				top: 0;
				width: 100%; /* Full width */
				height: 100%; /* Full height */
				overflow: auto; /* Enable scroll if needed */
				background-color: rgb(0,0,0);
				background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
			}
			
			.modal-header {
				background-color: #fefefe;
				margin: auto;
				padding-right: 10px;
				padding-left: 10px;
    			width: 650px;
    			height: 28px;
			}

			/* Modal Content */
			.modal-content {
				background-color: #fefefe;
				margin: auto;
				padding: 10px;
				border: 1px solid #888;
				width: 650px;
				height: 50%;
			}
			
			.modal-footer {
				background-color: #fefefe;
				margin: auto;
				padding-right: 10px;
				padding-left: 10px;
    			width: 650px;
    			height: 28px;
			}

			.prettyprint {
				width: 100%;
				height: 100%;
				padding: 0;
			}

			.cellView {
				cursor: pointer;
			}
		</style>
	</head>
	<body>
		<#list set><table id="logs">
		    <@showHeaders headers=headers />
			<#items as grp>
    			<@showGroup headers=headers grp=grp />
			</#items>
			</tbody>
			<tbody>
				<tr>
					<td colspan=${headers?size}><div class="menu">
						<button id="menu-info" title="Show information" class="btn" onclick="showInfo()"><i class="fa fa-info"></i></button>
						<button id="menu-main" title="Show all columns" class="btn" onclick="showAll()" disabled><i class="fa fa-eye"></i></button>
					</div></td>
				</tr>
			</tbody>
		</table>
		</#list>
		<div id="info" class="modal">
			<div class="modal-header"><button id="info-close-top" title="Close information" class="btn" onclick="hideInfo()"><i class="fa fa-close"></i></button></div>

  			<!-- Modal content -->
 			<div class="modal-content">
 				<textarea id="json" class="prettyprint">${info}</textarea>
 			</div>

 			<div class="modal-footer"><button id="info-close-bottom" title="Close information" class="btn" onclick="hideInfo()"><i class="fa fa-close"></i></button></div>
		</div>

		<script>
			const infoTxt = buildInfo();
			const infoView = document.getElementById("info");
			const infoTextArea = document.getElementById("json");

			function sortUp(colNum) {
				sortTable(colNum, false);
			}

			function sortDown(colNum) {
				sortTable(colNum, true);
			}

			function showHideColumn(colNum, show) {
				const table = document.getElementById('logs')
				const column = table.getElementsByTagName('col')[colNum]

				if (column) {
			    	column.style.visibility = show ? "" : "collapse";

					let menu = document.getElementById('menu-' + colNum)

					if (menu) {
						menu.style.display = show ? "" : "none";
			    	}

					menu = document.getElementById('menu-main')
					if (menu) {
						menu.disabled = false;
	    			}
				}
			}

			function showAll() {
				const table = document.getElementById('logs')
				const columns = table.getElementsByTagName('col')
				let colNum = -1;
				let menu;

				for (const column of columns) {		
					if (column) {
			    		column.style.visibility = "";

			    		menu = document.getElementById('menu-' + ++colNum)
						if (menu) {
							menu.style.display = "";
			    		}
					}
				}

				menu = document.getElementById('menu-main')
				if (menu) {
					menu.disabled = true;
	    		}
			}

			function sortTable(col, reverse) {
				const table = document.getElementById('logs')
				var tb = table.tBodies[0], // use `<tbody>` to ignore `<thead>` and `<tfoot>` rows

        		tr = Array.prototype.slice.call(tb.rows, 1), // put rows into array
		        	i;
			    reverse = -((+reverse) || -1);
    			tr = tr.sort(function (a, b) { // sort rows
        			return reverse // `-1 *` if want opposite order
            			* (a.cells[col].textContent.trim() // using `.textContent.trim()` for test
                		.	localeCompare(b.cells[col].textContent.trim())
               		);
    			});

    			for (i = 0; i < tr.length; ++i) {
    				tb.appendChild(tr[i]); // append each row in order
    			}
			}

			function showValue(value) {
				infoTextArea.value = value;
				infoView.style.display = "block";
			}

			function showInfo() {
				showValue(infoTxt);
			}

			function hideInfo() {
				infoView.style.display = "none";
			}

			function buildInfo() {
				let value = ${JSON.writeValueAsString(info)?no_esc};

				return JSON.stringify(value, null, 4);
			}
		</script>
	</body>
</html>

<#macro showGroup headers grp>
	<#list grp>
		<#items as line>
			<@showLine headers=headers line=line />
		</#items>
	</#list>
</#macro>

<#macro showLine headers line>
				<tr>
	<#list headers as header>
		<#assign notFound = true />
		<#list line as field>
			<#if header.id == field.fieldName>
					<td><@showValue value=field.formatedValue /></td>
				<#assign notFound = false />
				<#break />
			</#if>
		</#list>
		<#if notFound>
					<td></td>
		</#if>
	</#list>
				</tr>
</#macro>

<#macro showHeaders headers>
			<colgroup>
	<#list headers as header>
				<col style="width:${header.size};">
	</#list>
			</colgroup>
			<tbody>
				<tr class="headers">
	<#list headers as header>
					<td>${header.title} <div id="menu-${header?index}" class="menu">
						<button id="btn-${header?index}" title="Hide column" class="btn" onclick="showHideColumn(${header?index}, false)"><i class="fa fa-eye-slash"></i></button>
						<button id="down-${header?index}" title="Descending order" class="btn" onclick="sortDown(${header?index})"><i class="fa fa-caret-down"></i></button>
						<button id="up-${header?index}" title="Ascending order" class="btn" onclick="sortUp(${header?index})"><i class="fa fa-caret-up"></i></button>
					</div></td>
	</#list>
				</tr>
</#macro>

<#macro showValue value>
	<#if value?length <= maxCellLength>${value}<#else>
<div class="cellView" onclick="showValue('${value}')">${value?substring(0, maxCellLength)}...</div>
	</#if>
</#macro>
