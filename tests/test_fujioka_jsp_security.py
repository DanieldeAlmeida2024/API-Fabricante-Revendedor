import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
JSP = (ROOT / "fujioka/src/main/webapp/WEB-INF/views/index.jsp").read_text(encoding="utf-8")

class FujiokaJspSafety(unittest.TestCase):
    def test_data_attributes_escaped_at_html_boundary(self):
        for key in ("id", "produto", "categoria", "preco"):
            with self.subTest(key=key):
                self.assertIn("data-" + key + "=\"<c:out value='${produto." + key + "}'/>\"", JSP)
        self.assertIn('<c:out value="${produto.produto}"/>', JSP)
        self.assertIn('<c:out value="${produto.categoria}"/>', JSP)

    def test_untrusted_product_names_not_interpolated_into_javascript(self):
        self.assertNotIn('produto: "${produto.getProduto()}"', JSP)
        self.assertIn("document.querySelectorAll('#selectProduto option[data-id]')", JSP)
        self.assertIn("option.dataset.produto", JSP)
        self.assertIn("option.textContent =", JSP)
        inline = re.findall(r"<script(?:\\s[^>]*)?>(.*?)</script>", JSP, flags=re.S)
        self.assertTrue(inline)
        self.assertFalse(any("${produto." in js for js in inline), "JSP raw product data in inline JS")

    def test_no_unescaped_jsp_expressions_in_option(self):
        option_start = JSP.index('<c:forEach var="produto"')
        option_end = JSP.index('</c:forEach>', option_start)
        option = JSP[option_start:option_end]
        self.assertNotIn('data-produto="${produto.getProduto()}"', option)

    def test_env_not_tracked_in_head(self):
        self.assertFalse((ROOT / "Dell/.env").exists())
        self.assertFalse((ROOT / "fujioka/.env").exists())
        self.assertTrue((ROOT / "Dell/.env.example").exists())
        self.assertTrue((ROOT / "fujioka/.env.example").exists())

if __name__ == "__main__":
    unittest.main()
