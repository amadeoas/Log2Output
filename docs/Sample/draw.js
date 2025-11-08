const maxZoomUp = 4;
const minZoomDown = .4;
const y0 = 26;
const arrowSize = 6;
const DEFAULT_STYLE = '#AE81DB';
const APP_FONT = '16px serif';
const GRID_STYLE = '#D3D3D3';
const GRID_FILL_STYLE = '#A3A3A3';
const GRID_FONT = '10px serif';
const GRID_NUM_LINES = 2 + 4;
const GRID_DASH = [15, 5];
const ARROW_REQUEST = '#00008B';
const ARROW_RESPONSE = '#00008B';
const ARROW_SENT_REQUEST = '#6495ED';
const ARROW_SENT_RESPONSE = '#6495ED';
let W;
let H;
let data;
let zoom = 1.0;
let ctx;

function init() {
	const menus = document.getElementById('menu');
	const canvas = document.getElementById('draws');

	canvas.width = window.innerWidth;
	canvas.height = window.innerHeight - parseInt(menus.style.height);
	W = canvas.width;
	H = canvas.height - 6;
	ctx = canvas.getContext('2d');
	ctx.font = APP_FONT;
	ctx.strokeStyle = DEFAULT_STYLE;
	ctx.lineWidth = 1;

	document.getElementById('fInputData')
		.addEventListener('change', function selectedFileChanged() {
			if (this.files.length === 0) {
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
				drawAll(filename); // when the reader is done, the content is in reader.result
				setCursor('default');
	 		};
			reader.readAsText(this.files[0]);
   		});
}

function hide(id, hidden) {
  	let element = document.getElementById(id);
  			
    if (hidden) {
       element.setAttribute("hidden", "true");
    } else {
       element.removeAttribute("hidden");
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
		mesure = ctx.measureText(app.name);
		ctx.fillText(app.name, x0 - (mesure.width / 2), 16);
		drawVrLine(ctx, x0);
		for (const msg of app.msgs) {
			message(w, h, dateFrom, x0, msg, index);
		}
		x0 += w;
	}

	ctx.stroke();
	hide("view", false);
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
	let index =  getIndex(msg.app);
	let from_x = -1000;
	let fillStyle = ctx.strokeStyle;
	let to_x;
	let forward;

	if (index == -1) {
		index = 0;
	} else {
		index = index - appIndex;
	}

	if ("REQUEST" === msg.type) {
		from_x = (x0 + (w * index));
		if (index >= 0) {
			from_x -= w/2
		} else {
			from_x += w/2
		}
		to_x = x0;
		forward = true;
		fillStyle = ARROW_REQUEST;
	} else if ("RESPONSE" === msg.type) {
		from_x = x0;
		to_x = (x0 + (w * index));
		if (index >= 0) {
			to_x -= w/2
		} else {
			to_x += w/2
		}
		forward = false;
		fillStyle = ARROW_RESPONSE;
	} else if ("SENT-REQUEST" === msg.type) {
		from_x = x0;
		to_x = (x0 + (w * index))
		if (index >= 0) {
			to_x -= w/2
		} else {
			to_x += w/2
		}
		forward = true;
		fillStyle = ARROW_SENT_REQUEST;
	} else if ("SENT-RESPONSE" === msg.type) {
		from_x = (x0 + (w * index));
		if (index >= 0) {
			from_x -= w/2
		} else {
			from_x += w/2
		}
		to_x = x0;
		forward = false;
		fillStyle = ARROW_SENT_REQUEST;
	}

	if (from_x != -1000) {
		const ms = getMilliseconds(msg.on) - dateFrom;
		const y = y0 + (h * ms);
		const oldFillStyle = ctx.fillStyle;
		const oldStrokeStyle = ctx.strokeStyle;

		ctx.fillStyle = fillStyle;
		ctx.strokeStyle = fillStyle;
		arrow(from_x, y, to_x, arrowSize, forward);
		ctx.fillStyle = oldFillStyle;
		ctx.strokeStyle = oldStrokeStyle;
	}
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

function arrow(from_x, y0, to_x, r, forward) {
	var x;
	var y;
	
	if (forward) {
		r = -r;
	} else {
	}
	ctx.moveTo(from_x, y0);

	x = to_x + r;
	ctx.lineTo(x, y0);

	ctx.stroke();

	ctx.beginPath();

	y = y0 - (r / 2);
	ctx.lineTo(x, y);

	ctx.lineTo(to_x, y0);

	y = y0 + (r / 2);
	ctx.lineTo(x, y);

	x = to_x + r;
	ctx.lineTo(x, y0);

	ctx.closePath();
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
