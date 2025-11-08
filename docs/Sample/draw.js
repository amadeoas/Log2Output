let data;
let zoom = 1;
let maxZoomUp = 4;
let minZoomDown = .4;
let ctx;

function init() {
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

	ctx = canvas.getContext('2d');
	// Clear canvas
    ctx.clearRect(0, 0, canvas.width, canvas.height);
	ctx.scale(zoom, zoom);

	const W = canvas.width;
	const w = W / data.apps.length;
	const dateFrom = getMilliseconds(data.dateFrom);
	let x0 = w / 2;
	const y0 = 40;
	const H = canvas.height;
	const h = (H - y0) / (getMilliseconds(data.dateTo) - dateFrom);
	let index = -1;

	ctx.font = "28px serif";
	ctx.strokeStyle = '#AE81DB'
	for (const app of data.apps) {
		++index;
		ctx.fillText(app.name, x0, 26);
		drawVrLine(ctx, x0, y0, H);
		for (const msg of app.msgs) {
			message(ctx, w, h, dateFrom, x0, y0, msg, index);
		}
		x0 += w;
	}

	ctx.stroke();
	hide("view", false);
}

function getMilliseconds(datetime) {
	const d = new Date(datetime);

	return d.getTime();
}

function drawVrLine(ctx, x0, y0, height) {
	ctx.beginPath();
	ctx.moveTo(x0, y0);
	ctx.lineTo(x0, height);

	ctx.stroke();
}

function message(ctx, w, h, dateFrom, x0, y0, msg, appIndex) {
	let index =  getIndex(msg.app);
	let from_x = -1000;
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
	} else if ("RESPONSE" === msg.type) {
		from_x = x0;
		to_x = (x0 + (w * index));
		if (index >= 0) {
			to_x -= w/2
		} else {
			to_x += w/2
		}
		forward = false;
	} else if ("SENT-REQUEST" === msg.type) {
		from_x = x0;
		to_x = (x0 + (w * index))
		if (index >= 0) {
			to_x -= w/2
		} else {
			to_x += w/2
		}
		forward = true;
	} else if ("SENT-RESPONSE" === msg.type) {
		from_x = (x0 + (w * index));
		if (index >= 0) {
			from_x -= w/2
		} else {
			from_x += w/2
		}
		to_x = x0;
		forward = false;
	}

	if (from_x != -1000) {
		const ms = getMilliseconds(msg.on) - dateFrom;
		const y = y0 + (h * ms);

		arrow(ctx, from_x, y, to_x, 8, forward);
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

function arrow(ctx, from_x, y0, to_x, r, forward) {
	var x;
	var y;
	
	if (forward) {
		r = -r;
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
	zoom += .1;
	drawAll_();
	if (zoom >= maxZoomUp) {
		let element = document.getElementById('zoomUp');

		element.disabled = true;
	}
}

function zoomItDown() {
	if (zoom >= minZoomDown) {
		return;
	}
	if (zoom >= maxZoomUp) {
		let element = document.getElementById('zoomUp');

		element.disabled = false;
	}
	zoom -= .1;
	drawAll_();
	if (zoom <= minZoomDown) {
		let element = document.getElementById('zoomSDown');

		element.disabled = true;
	}
}