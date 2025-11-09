const maxZoomUp = 4;
const minZoomDown = .4;
const y0 = 26;
const arrowSize = 10;
const DEFAULT_STYLE = '#AE81DB';
const APP_FONT = '16px serif';
const GRID_STYLE = '#D3D3D3';
const GRID_FILL_STYLE = '#A3A3A3';
const GRID_FONT = '10px serif';
const GRID_NUM_LINES = 2 + 4; // 2 must be kept but 4 may be change to increase the number of grid lines
const GRID_DASH = [15, 5];
const ARROW_REQUEST = '#00008B';
const ARROW_RESPONSE = '#00008B';
const ARROW_SENT_REQUEST = '#6495ED';
const ARROW_SENT_RESPONSE = '#6495ED';
const MSG_LINE_STYLE = '#D2691E';
const MSG_LINE_LENGTH = 6;
const ay = 1; // extra heigh for line detection

const ignore = ['name', 'msgs', 'paths', 'parent', 'path'];
const fields = [{name: 'msg', type: 'textarea'}];

let W;
let H;
let data;
let zoom = 1.0;
let canvas;
let ctx;


function init() {
	const menus = document.getElementById('menu');

	canvas = document.getElementById('draws');
	canvas.width = window.innerWidth;
	canvas.height = window.innerHeight - parseInt(menus.style.height) - 8;
	W = canvas.width;
	H = canvas.height - 6;
	ctx = canvas.getContext('2d');
	ctx.font = APP_FONT;
	ctx.strokeStyle = DEFAULT_STYLE;
	ctx.lineWidth = 1;
	canvas.addEventListener("mousemove", function(e) {handleMouseMove(e);}, false);
	canvas.addEventListener("click", function(e) {onclickCavas(e);}, false);
	showScale();

	document.getElementById('fInputData')
		.addEventListener('change', function selectedFileChanged() {
			if (this.files.length == 0) {
	    		console.log('No file selected.');

		    	return;
  			}
			hide('finput', true);
			console.log("INFO: Loading data file \"" + this.files[0].name + "\"...");

  			const reader = new FileReader();
  			const filename = this.files[0].name;

			setCursor('progress');
			reader.onload = function fileReadCompleted() {
				console.log("INFO: Data file \"" + filename + "\" was loaded");

				data = JSON.parse(reader.result);
				if (data.grid === undefined) {
					data.grid = false;
				}

				// Init data
				for (const app of data.apps) {
					app.numMsgs = app.msgs.length;
					for (const msg of app.msgs) {
						msg.name = app.name;
						msg.parent = app;
					}
				}
				drawAll(filename); // when the reader is done, the content is in reader.result
				setCursor('default');
	 		};
			reader.readAsText(this.files[0]);
   		});
}

function hide(id, hidden) {
  	let element = document.getElementById(id);
  			
    if (hidden) {
       element.style.display = 'none';
    } else {
       element.style.display = 'block';
    }
}

function setCursor(cursor) {
	document.getElementsByTagName("body")[0].style.cursor = cursor;
}

function drawAll(filename) {
	console.log("INFO: Drawing data from file \"" + filename + "\"...");

	drawAll_();

	console.log("INFO: Data from file \"" + filename + "\" was drawn");
}


function drawAll_() {
	const canvas = document.getElementById("draws");

	// Clear canvas
	ctx.clearRect(0, 0, W, H + 6);
	ctx.scale(zoom, zoom);

	const w = W / data.apps.length;
	let x0 = w / 2;
	const dateFrom = getMilliseconds(data.dateFrom);
	const h = (H - y0) / (getMilliseconds(data.dateTo) - dateFrom);
	let index = -1;
	let mesure;

	if (data.grid) {
		// Draw grid
		drawGrid();
	}

	for (const app of data.apps) {
		++index;
		drawTitle(app, x0);
		drawVrLine(ctx, x0);
		for (const msg of app.msgs) {
			message(w, h, dateFrom, x0, msg, index);
		}
		x0 += w;
	}

	ctx.stroke();
	hide("view", false);
}

function drawTitle(app, x0) {
	if (app.path === undefined) {
		let mesure = ctx.measureText(app.name);
		let aWidth = Math.round(mesure.width / 2);

		app.path = new Array();
		app.path[0] = {x: x0 - aWidth, y: 16};
		app.path[1] = {x: app.path[0].x, y: 16 - Math.round(mesure.actualBoundingBoxAscent)};
		app.path[2] = {x: x0 + aWidth, y: app.path[1].y};
		app.path[3] = {x: app.path[2].x, y: 16 + Math.round(mesure.actualBoundingBoxDescent)};
		app.path[4] = {x: app.path[1].x, y: app.path[3].y};
	}

	ctx.fillText(app.name, app.path[0].x, app.path[0].y);
}

function drawGrid() {
	const oldFont = ctx.font;
	const oldStype = ctx.fillStyle;
	const oldColor = ctx.strokeStyle;
	let w;

	ctx.strokeStyle = GRID_STYLE;
	ctx.setLineDash(GRID_DASH);

	drawTxt(oldFont, oldStype, data.dateFrom.replace('T', ' '), W, y0 - 3);
	ctx.beginPath();
	ctx.moveTo(0, y0);
	ctx.lineTo(W, y0);
	ctx.stroke();

	const num = GRID_NUM_LINES - 1;
	const dateFrom = getMilliseconds(data.dateFrom);
	const dateInc = getMilliseconds(data.dateTo) - dateFrom;
	const inc = dateInc / num;
	const h = (H - y0) / num;
	let y = y0;

	for (let i = 1; i < num; ++i) {
		y += h;
		drawTxt(oldFont, oldStype, dateTxt(data.dateFrom + (i * inc)), W, y - 3);
		ctx.beginPath();
		ctx.moveTo(0, y);
		ctx.lineTo(W, y);
		ctx.stroke();
	}

	drawTxt(oldFont, oldStype, data.dateTo.replace('T', ' '), W, H - 3);
	ctx.beginPath();
	ctx.moveTo(0, H);
	ctx.lineTo(W, H);
	ctx.stroke();

	ctx.strokeStyle = oldColor;
	ctx.setLineDash([]);
}

function drawTxt(oldFont, oldStype, txt, x, y) {
	ctx.font = GRID_FONT;
	ctx.fillStyle = GRID_FILL_STYLE;
	w = ctx.measureText(txt);
	ctx.fillText(txt, x - w.width - 3, y);
	ctx.font = oldFont;
	ctx.fillStyle = oldStype;
}

function getMilliseconds(datetime) {
	const d = new Date(datetime);

	return d.getTime();
}

function drawVrLine(ctx, x) {
	ctx.beginPath();
	ctx.moveTo(x, y0);
	ctx.lineTo(x, H);

	ctx.stroke();
}

function message(w, h, dateFrom, x0, msg, appIndex) {
	let index = getIndex(msg.app);
	let from_x;
	let fillStyle = ctx.strokeStyle;
	let to_x;
	let forward;

	if (index == -1) {
		index = 0;
	} else {
		index = index - appIndex;
	}

	if ("REQUEST" === msg.type) {
		from_x = Math.round(x0 + (w * index));
		if (index >= 0) {
			from_x -= w/2;
		} else {
			from_x += w/2;
		}
		from_x = Math.round(from_x);
		to_x = x0;
		forward = true;
		fillStyle = ARROW_REQUEST;
	} else if ("RESPONSE" === msg.type) {
		from_x = x0;
		to_x = Math.round(x0 + (w * index));
		if (index >= 0) {
			to_x -= w/2;
		} else {
			to_x += w/2;
		}
		to_x = Math.round(to_x);
		forward = false;
		fillStyle = ARROW_RESPONSE;
	} else if ("SENT-REQUEST" === msg.type) {
		from_x = x0;
		to_x = Math.round(x0 + (w * index))
		if (index >= 0) {
			to_x -= w/2;
		} else {
			to_x += w/2;
		}
		to_x = Math.round(to_x);
		forward = true;
		fillStyle = ARROW_SENT_REQUEST;
	} else if ("SENT-RESPONSE" === msg.type) {
		from_x = Math.round(x0 + (w * index));
		if (index >= 0) {
			from_x -= w/2;
		} else {
			from_x += w/2;
		}
		from_x = Math.round(from_x);
		to_x = x0;
		forward = false;
		fillStyle = ARROW_SENT_REQUEST;
	}

	const ms = getMilliseconds(msg.on) - dateFrom;
	const y = (y0 + (h * ms));
	const oldFillStyle = ctx.fillStyle;
	const oldStrokeStyle = ctx.strokeStyle;

	ctx.fillStyle = fillStyle;
	ctx.strokeStyle = fillStyle;
	arrow(msg, x0, from_x, y, to_x, arrowSize, forward);
	ctx.fillStyle = oldFillStyle;
	ctx.strokeStyle = oldStrokeStyle;
}

function getIndex(appName) {
	if (null === appName || "" === appName) {
		return 0;
	}

	let index = -1;

	for (const app of data.apps) {
		++index;
		if (appName === app.name) {
			return index;
		}
	}

	return -1;
}

function arrow(msg, x0, from_x, y, to_x, r, forward) {
	if (msg.paths == undefined) {
		var x;

		msg.paths = new Array();

		// Line
		msg.paths[0] = new Array();
		x = x0 - MSG_LINE_LENGTH;
		msg.paths[0][0] = {x: x, y: y};
		x = x0 + MSG_LINE_LENGTH;
		msg.paths[0][1] = {x: x, y: y};

		if (msg.type !== undefined) {
			var y1;

			if (forward) {
				r = -r;
			}

			// Line
			msg.paths[1] = new Array();
			msg.paths[1][0] = {x: from_x, y: y};
			x = to_x + r;
			msg.paths[1][1] = {x: x, y: y};

			// Arrow
			msg.paths[2] = new Array();
			msg.paths[2][0] = msg.paths[1][1];
			y1 = Math.round(y - (r / 2));
			msg.paths[2][1] = {x: x, y: y1};
			msg.paths[2][2] = {x: to_x, y: y};
			y1 = Math.round(y + (r / 2));
			msg.paths[2][3] = {x: x, y: y1};
			msg.paths[2][4] = msg.paths[1][1];
		}
	}

	const oldFillStyle = ctx.fillStyle;
	const oldStrokeStyle = ctx.strokeStyle;

	ctx.fillStyle = MSG_LINE_STYLE;
	ctx.strokeStyle = MSG_LINE_STYLE;
	definePath(msg.paths[0]);
	ctx.stroke();
	ctx.fillStyle = oldFillStyle;
	ctx.strokeStyle = oldStrokeStyle;
	if (msg.paths.length == 1) {
		return;
	}

	definePath(msg.paths[1]);
	ctx.stroke();

	definePath(msg.paths[2]);
	ctx.fill();
}

function closeView() {
	hide('view', true);

	const input = document.getElementById('fInputData');

	input.value = null;
	hide('finput', false);
}

function zoomItUp() {
	if (zoom >= maxZoomUp) {
		return;
	}

	if (zoom <= minZoomDown) {
		let element = document.getElementById('zoomDown');

		element.disabled = false;
	}
	drawAllZoom(.1);
	if (zoom >= maxZoomUp) {
		let element = document.getElementById('zoomUp');

		element.disabled = true;
	}
}

function zoomItDown() {
	if (zoom <= minZoomDown) {
		return;
	}
	if (zoom >= maxZoomUp) {
		let element = document.getElementById('zoomUp');

		element.disabled = false;
	}
	drawAllZoom(-.1);
	if (zoom <= minZoomDown) {
		let element = document.getElementById('zoomDown');

		element.disabled = true;
	}
}

function drawAllZoom(inc) {
	let z = zoom + inc;

	zoom = z / zoom;
	drawAll_();
	zoom = z;
	showScale();
}

function showScale() {
	let scale = document.getElementById('scale');

	scale.value = zoom;
}

function switchIt() {
	const inp = document.getElementById('togBtn');
	const checked = inp.checked;

	data.grid = checked;
	drawAll_();
}

function dateTxt(datetime) {
	const today = new Date(datetime);
	var day = addZeros(today.getDate(), 2);
	var month = addZeros(today.getMonth() + 1, 2);
	var year = addZeros(today.getFullYear(), 4);
	var hour = addZeros(today.getHours(), 2);
	var minutes = addZeros(today.getMinutes(), 2);
	var seconds = addZeros(today.getSeconds(), 2);
	var ms = addZeros(today.getMilliseconds(), 3);

	return day + "-" + month + "-" + year + " " + hour + ":" + minutes + ":" + seconds + "." + ms;
}

function addZeros(value, length) {
	var data = value + "";

	while (data.length < length) {
		data = "0" + data;
	}

	return data;
}

function handleMouseMove(e) {
	// Tell the browser we're handling this event
	e.preventDefault();
	e.stopPropagation();

	// Calculate mouse position
    const mouseX = getMouseX(e)
    const mouseY = getMouseY(e);

	displayCoord(mouseX, mouseY);
	if (getApp(mouseX, mouseY) != null || getMsg(mouseX, mouseY) != null) {
		canvas.style.cursor = 'pointer';

		return;
	}
	canvas.style.cursor = 'default';
}

function getApp(x, y) {
	for (const app of data.apps) {
		if (x >= app.path[1].x && x <= app.path[2].x
				&& y >= app.path[1].y && y <= app.path[3].y) {
			return app;
		}
	}

	return null;
}

function getMouseX(e) {
	const rect = canvas.getBoundingClientRect();
    const scaleX = canvas.width / rect.width;    // relationship between actual and displayed width

    return Math.round((e.clientX - rect.left) * scaleX);
}

function getMouseY(e) {
	const rect = canvas.getBoundingClientRect();
    const scaleY = canvas.height / rect.height;  // relationship between actual and displayed height

    return Math.round((e.clientY - rect.top) * scaleY);
}

function displayCoord(x, y) {
	let xCoord = document.getElementById('xCoord');
	let yCoord = document.getElementById('yCoord');

	xCoord.value = x;
	yCoord.value = y;
}

function getMsg(x, y) {
	for (const app  of data.apps) {
		for (const msg of app.msgs) {
			for (const path of msg.paths) {
				if (isPointInPath(path, x, y)) {
					return msg;
				}
			}
		}
	}

	return null;
}

function isPointInPath(path, x, y) {
	if (path.length == 2) {
		// Line
		let p0 = path[0];

		if (y == p0.y || y == (p0.y - ay)) {
			let p1 = path[1];
			let min = Math.min(p0.x, p1.x);
			let max = Math.max(p0.x, p1.x);

			if (x >= min && x <= max) {
				return true;
			}
		}

		return false;
	}

	// Triangle
	return pointInTriangle(path, x, y);
}

function pointInTriangle(path, x, y) {
	const p = {x: x, y: y};
	const p0 = path[0];
	const p1 = path[1];
	const p2 = path[2];
    let s = (p0.x - p2.x) * (p.y - p2.y) - (p0.y - p2.y) * (p.x - p2.x);
    var t = (p1.x - p0.x) * (p.y - p0.y) - (p1.y - p0.y) * (p.x - p0.x);

    if ((s < 0) != (t < 0) && s != 0 && t != 0) {
        return false;
    }

    let d = (p2.x - p1.x) * (p.y - p1.y) - (p2.y - p1.y) * (p.x - p1.x);

    return d == 0 || (d < 0) == (s + t <= 0);
}

function definePath(p) {
	ctx.beginPath();
	ctx.moveTo(p[0].x, p[0].y);
	for (var i = 1; i < p.length; ++i) {
		ctx.lineTo(p[i].x,p[i].y);
	}
	ctx.closePath();
}

function getLabelTxt(value) {
	let v = '';
	let from = 0;
	let to = 1;

	value = value.charAt(0).toUpperCase() + value.substring(1, value.length);
	do {
		while (to < value.length && isLowerCase(value, to)) {
			++to;
		}

		if (from < to) {
			if (v.length > 0) {
				v += ' ';
			}
			v += value.substring(from, to);
		}
		from = to;
	} while (++to < value.length);

	return v;
}

function isLowerCase(value, index) {
	let c = value.charAt(index);

    if (c >= 'a' && c <= 'z') {
        return true;
    }

	return false;
}

function showElement(value) {
	var fieldNames = Object.keys(value);
	let popupFields;
	let div
	let element;
	let def;

	popupFields = document.getElementById('popupFields');
	popupFields.innerHTML = '';

	element = document.getElementById('appName');
	element.innerHTML = value.name;

	outerlook:
	for (const fieldName of fieldNames) {
		for (const i of ignore) {
			if (fieldName === i) {
				continue outerlook;
			}
		}

		div = document.createElement("div");
		div.id = fieldName + "Data";

		element = document.createElement("label");
		element.classList.add('inputLabel');
		element.innerHTML = getLabelTxt(fieldName) + ": ";
		div.appendChild(element);

		def = null;
		for (const field of fields) {
			if (fieldName === field.name) {
				def = field;

				continue;
			}
		}

		if (def === null) {
			div.classList.add('dataLine');

			element = document.createElement("input");
			element.id = "in" + div.id;
			element.classList.add('dataLine');
			element.classList.add('allLine');
			element.type = 'text';
			element.disabled = true;
			element.value = value[fieldName];
		} else {
			let d = document.createElement("div");

			d.classList.add('divTextareaData');
			element = document.createElement(def.type);
			element.id = "in" + div.id;
			element.classList.add('textareaData');
			element.disabled = true;
			element.value = value[fieldName];
			d.appendChild(element);

			element = d;
		}
		div.appendChild(element);
		popupFields.appendChild(div);
	}

	element = document.getElementById('popup');
	element.style.display = 'block';
	element = document.getElementById('popupFade');
	element.style.display = 'flex';
}

function onclickCavas(e) {
	// Tell the browser we're handling this event
	e.preventDefault();
	e.stopPropagation();

	if (canvas.style.cursor === 'pointer') {
		let msg = getData(getMouseX(e), getMouseY(e));

		showElement(msg);
	}
}

function getData(x, y) {
	let element = getApp(x, y);

	if (element != null) {
		return element;
	}

	element = getMsg(x, y);
	if (element != null) {
		return element;
	}
	
	return null;
}

function closePopup() {
	let element = document.getElementById('popup');

	element.style.display = 'none';
	element = document.getElementById('popupFade');
	element.style.display = 'none';
}
