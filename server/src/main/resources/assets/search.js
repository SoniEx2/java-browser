function updateSearch(items, target) {
    console.log("Updating search...");
    let html = "";
    for (const a of items) {
        html += "<li><a href='/" + a.artifactId + "/" + a.path + "#" + a.binding + "'>";
        let off = 0;
        for (let i = 0; i < a.components.length; i++) {
            const componentLength = a.components[i];
            const component = a.binding.substring(off, off + componentLength);
            const bold = a.match[i];
            if (bold > 0) {
                html += "<b>" + component.substring(0, bold) + "</b>";
            }
            html += component.substring(bold);
            off += componentLength;
        }
        html += "</a></li>";
    }
    target.innerHTML = html;
}

function loadQuery(query, artifactId, consumer) {
    const req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState === XMLHttpRequest.DONE) {
            let resp = JSON.parse(req.responseText);
            consumer(resp);
        }
    };
    req.open("GET", "/api/search/" + encodeURI(query) + "?artifactId=" + encodeURI(artifactId), true);
    req.send();
}

window.onload = function () {
    for (const searchField of document.querySelectorAll(".search")) {
        const target = document.querySelector(searchField.getAttribute("data-target"));
        const artifactId = searchField.getAttribute("data-artifact-id");
        const update = function() {
            loadQuery(searchField.value, artifactId, function (data) {
                updateSearch(data.items, target);
            });
        };
        searchField.addEventListener('input', update);
        searchField.addEventListener('keypress', function (e) {
            if ((e.which || e.keyCode) === 13) { // enter
                target.querySelector("a").click();
                e.preventDefault();
            }
        });
        update();
    }
};