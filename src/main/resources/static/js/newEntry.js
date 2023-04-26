let categoryInput = document.querySelector("#category");
let optionElemArr = document.querySelectorAll("#categories option");
let categoryInputPattern = "^(";
optionElemArr.forEach( (item) => {
    categoryInputPattern+= `${item.value}|`;
});
categoryInputPattern = categoryInputPattern.slice(0, -1);
categoryInputPattern += ')$';

categoryInput.setAttribute("pattern",categoryInputPattern);
