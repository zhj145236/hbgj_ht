function getMenuTree() {
	var root = {
		id : 0,
		name : "全部",
		open : true,
	};

	$.ajax({
		type : 'get',
		url : '/org/all',
		contentType : "application/json; charset=utf-8",
		async : false,
		success : function(data) {
			var length = data.length;
			var children = [];
			for (var i = 0; i < length; i++) {
				var d = data[i];
				var node = createNode(d);
				children[i] = node;
			}

			root.children = children;
		}
	});

	return root;
}




function createNode(d) {
	var id = d['id'];
	var pId = d['parentId'];
	var name = d['orgName'];
	var child = d['child'];


	var node = {
		open : true,
		id : id,
		name : name,
		pId : pId,
	};

	if (child != null) {
		var length = child.length;
		if (length > 0) {
			var children = [];
			for (var i = 0; i < length; i++) {
				children[i] = createNode(child[i]);
			}

			node.children = children;
		}

	}
	return node;
}



function getSettting() {
	var setting = {
	/*	check : {
			enable : true,
			chkboxType : {
				"Y" : "ps",
				"N" : "ps"
			}
		},*/
		async : {
			enable : true,
		},
		data : {
			simpleData : {
				enable : true,
				idKey : "id",
				pIdKey : "pId",
				rootPId : 0
			}
		},
		callback : {
			onCheck : zTreeOnCheck
		}
	};

	return setting;
}

function zTreeOnCheck(event, treeId, treeNode) {
alert(JSON.stringify(treeNode));
	console.info(treeNode.id + ", " + treeNode.name + "," + treeNode.checked
			+ "," + treeNode.pId);
	//console.log(JSON.stringify(treeNode));
}