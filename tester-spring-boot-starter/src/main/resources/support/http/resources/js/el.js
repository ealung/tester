function parameter(k,v) {
    this.k=k;
    this.v=v;
}
function getParameters(parent) {
    var parameters=new Array();
    var i=0;
    $(parent).find(".form-group").each(function () {
        var k = $(this).find('label').text();
        var v = $(this).find('input').val();
        var p=new parameter(k,v);
        parameters[i]=p;
        i++;
    });
    return parameters;
}
function getUrl(elurl, parameters) {
    var start = elurl.indexOf("{");
    if (start === -1) {
        return elurl;
    }
    var end = elurl.indexOf("}");
    var elName = elurl.substring(start+1, end);
    parameters.forEach(function (parameter) {
        if (parameter.k === elName) {
            elurl = elurl.substring(0, start) + parameter.v + elurl.substring(end + 1, elurl.length);
            return getUrl(elurl, parameters);
        }
    });
    return elurl;
}
function formatUrl(parent,url) {
    var parameters = getParameters(parent);
    return getUrl(url,parameters);
}