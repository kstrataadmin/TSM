var checked = false;
function checkAll() {
	var elements = new Array();
	var j = 0;
	var aa = document.getElementById('mainTemplateFormID');
	
	for ( var i = 0; i < aa.elements.length; i++) {
		if (aa.elements[i].type && aa.elements[i].type == 'checkbox'
				&& aa.elements[i].name.indexOf('select_all') != -1 && !aa.elements[i].disabled) {
			elements[j] = aa.elements[i].checked;
			j++;
		}
	}
	for ( var i = 0; i < elements.length; i++) {
		if (elements[i]) {
			for ( var k = 0; k < aa.elements.length; k++) {
				if (aa.elements[k].type
						&& aa.elements[k].type == 'checkbox'
						&& aa.elements[k].name.indexOf('select_one') != -1 && !aa.elements[k].disabled) {
					aa.elements[k].checked = true;
				}
			}
		} else {
			for ( var k = 0; k < aa.elements.length; k++) {
				if (aa.elements[k].type
						&& aa.elements[k].type == 'checkbox'
						&& aa.elements[k].name.indexOf('select_one') != -1 && !aa.elements[k].disabled) {
					aa.elements[k].checked = false;
				}
			}
		}
	}
}

function unCheckedAll() {
	var elements = new Array();
	var j = 0;
	var aa = document.getElementById('mainTemplateFormID');

	for ( var i = 0; i < aa.elements.length; i++) {
		if (aa.elements[i].type && aa.elements[i].type == 'checkbox'
				&& aa.elements[i].name.indexOf('select_one') != -1) {
			elements[j] = aa.elements[i].checked;
			j++;
		}
	}
	for ( var i = 0; i < elements.length; i++) {
		if (elements[i]) {
			for ( var k = 0; k < aa.elements.length; k++) {
				if (aa.elements[k].type
						&& aa.elements[k].type == 'checkbox'
						&& aa.elements[k].name.indexOf('select_all') != -1) {
					aa.elements[k].checked = true;
				}
			}
		} else {
			for ( var k = 0; k < aa.elements.length; k++) {
				if (aa.elements[k].type
						&& aa.elements[k].type == 'checkbox'
						&& aa.elements[k].name.indexOf('select_all') != -1) {
					aa.elements[k].checked = false;
					return;
				}
			}
		}
	}

}
