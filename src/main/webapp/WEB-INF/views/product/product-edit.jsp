<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="products" scope="request"/>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Ch&#7881;nh s&#7917;a h&#224;ng h&#243;a"/>
</jsp:include>
<jsp:include page="../common/navbar.jsp"/>

<div class="kr-page">
    <div class="kr-main">
        <aside class="kr-sidebar">
            <div style="padding:12px 16px; font-size:13px; font-weight:700; border-bottom:1px solid #e8eaed; background:#f8fafc;">QU&#7842;N L&#221; H&#192;NG H&#211;A</div>
            <a href="${ctx}/admin/products" style="display:flex; align-items:center; gap:10px; padding:10px 16px; font-size:13px; color:#15171a; border-bottom:1px solid #f5f5f5; text-decoration:none;">
                <svg style="width:16px;height:16px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/></svg>
                Danh s&#225;ch h&#224;ng h&#243;a
            </a>
            <a href="${ctx}/admin/products?action=create" style="display:flex; align-items:center; gap:10px; padding:10px 16px; font-size:13px; color:#15171a; border-bottom:1px solid #f5f5f5; text-decoration:none;">
                <svg style="width:16px;height:16px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                Th&#234;m m&#7899;i h&#224;ng h&#243;a
            </a>
        </aside>

        <div class="kr-content" style="border:none; background:transparent; overflow:visible;">
            <jsp:include page="../common/toast.jsp"/>

            <style>
                .kr-tab-radio { display:none; }
                .kr-tab-panel { display:none; }
                #product-tab-general:checked ~ .kr-tab-card .kr-tab-general,
                #product-tab-history:checked ~ .kr-tab-card .kr-tab-history { display:block; }
                #product-tab-general:checked ~ .kr-tab-card label[for="product-tab-general"],
                #product-tab-history:checked ~ .kr-tab-card label[for="product-tab-history"] {
                    color:#0070f4; background:#fff; border-bottom-color:#fff;
                }
            </style>

            <form method="POST" action="${ctx}/admin/products" autocomplete="off">
                <input type="hidden" name="action" value="update"/>
                <input type="hidden" name="productId" value="${product.productId}"/>

                <div style="display:flex; align-items:flex-start; justify-content:space-between; gap:16px; margin-bottom:16px; flex-wrap:wrap;">
                    <div>
                        <div style="font-size:13px; color:#5f6368; display:flex; align-items:center; gap:6px; margin-bottom:8px;">
                            <a href="${ctx}/admin/products" style="color:#0070f4; text-decoration:none;">H&#224;ng h&#243;a</a>
                            <span style="color:#9aa0a6;">/</span>
                            <span>Ch&#7881;nh s&#7917;a h&#224;ng h&#243;a</span>
                        </div>
                        <h1 class="kr-page-title" style="margin:0;">Ch&#7881;nh s&#7917;a h&#224;ng h&#243;a</h1>
                    </div>
                    <div style="display:flex; align-items:center; gap:8px; flex-shrink:0;">
                        <button type="submit" class="kr-btn kr-btn-primary">L&#432;u thay &#273;&#7893;i</button>
                        <a class="kr-btn" href="${ctx}/admin/products">Quay l&#7841;i</a>
                    </div>
                </div>

                <input class="kr-tab-radio" type="radio" id="product-tab-general" name="productTab" checked />
                <input class="kr-tab-radio" type="radio" id="product-tab-history" name="productTab" />

                <div class="kr-tab-card" style="background:#fff; border:1px solid #e8eaed; border-radius:10px; overflow:hidden;">
                    <div style="display:flex; align-items:flex-end; gap:8px; padding:0 24px; border-bottom:1px solid #e8eaed; background:#f8fafc;">
                        <label for="product-tab-general" style="font-size:15px; font-weight:700; color:#5f6368; padding:14px 18px; margin-top:12px; margin-bottom:-1px; background:#f8fafc; border:1px solid #e8eaed; border-radius:8px 8px 0 0; cursor:pointer;">Th&#244;ng tin chung</label>
                        <label for="product-tab-history" style="font-size:15px; font-weight:700; color:#5f6368; padding:14px 18px; margin-top:12px; margin-bottom:-1px; background:#f8fafc; border:1px solid #e8eaed; border-radius:8px 8px 0 0; cursor:pointer;">L&#7883;ch s&#7917; ho&#7841;t &#273;&#7897;ng</label>
                    </div>

                    <%-- Tab: Thong tin chung --%>
                    <div class="kr-tab-panel kr-tab-general" style="padding:24px;">
                        <div style="display:flex; flex-direction:column; gap:24px;">

                            <section style="display:grid; grid-template-columns:1fr 1fr; gap:16px;">
                                <div style="display:flex; flex-direction:column; gap:6px;">
                                    <label style="font-size:13px; font-weight:600;">M&#227; h&#224;ng / SKU <span style="color:#ef4444;">*</span></label>
                                    <input type="text" name="sku" required maxlength="50" value="<c:out value='${product.sku}'/>" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; box-sizing:border-box;"/>
                                </div>
                                <div style="display:flex; flex-direction:column; gap:6px;">
                                    <label style="font-size:13px; font-weight:600;">T&#234;n h&#224;ng h&#243;a <span style="color:#ef4444;">*</span></label>
                                    <input type="text" name="name" required maxlength="200" value="<c:out value='${product.productName}'/>" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; box-sizing:border-box;"/>
                                </div>
                                <div style="display:flex; flex-direction:column; gap:6px;">
                                    <label style="font-size:13px; font-weight:600;">Nh&#243;m h&#224;ng</label>
                                    <select name="categoryId" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; background:#fff; box-sizing:border-box;">
                                        <option value="">Ch&#7885;n nh&#243;m h&#224;ng</option>
                                        <c:forEach var="category" items="${categories}">
                                            <option value="${category.categoryId}" <c:if test="${category.categoryId == product.categoryId}">selected</c:if>><c:out value="${category.name}"/></option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div style="display:flex; flex-direction:column; gap:6px;">
                                    <label style="font-size:13px; font-weight:600;">Tr&#7841;ng th&#225;i</label>
                                    <select name="status" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; background:#fff; box-sizing:border-box;">
                                        <option value="active" <c:if test="${product.status == 'active'}">selected</c:if>>&#272;ang kinh doanh</option>
                                        <option value="inactive" <c:if test="${product.status == 'inactive'}">selected</c:if>>Ng&#432;ng kinh doanh</option>
                                    </select>
                                </div>
                            </section>

                            <section>
                                <div style="font-size:15px; font-weight:700; margin-bottom:14px; padding-bottom:10px; border-bottom:1px solid #e8eaed;">Gi&#225; &amp; T&#7891;n kho</div>
                                <div style="display:grid; grid-template-columns:1fr 1fr 1fr; gap:16px;">
                                    <div style="display:flex; flex-direction:column; gap:6px;">
                                        <label style="font-size:13px; font-weight:600;">Gi&#225; b&#225;n <span style="color:#ef4444;">*</span></label>
                                        <input type="number" name="price" required min="0" step="1000" value="<c:out value='${product.price}'/>" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; box-sizing:border-box;"/>
                                    </div>
                                    <div style="display:flex; flex-direction:column; gap:6px;">
                                        <label style="font-size:13px; font-weight:600;">Gi&#225; v&#7889;n</label>
                                        <input type="number" name="costPrice" min="0" step="1000" value="<c:out value='${product.costPrice}'/>" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; box-sizing:border-box;"/>
                                    </div>
                                    <div style="display:flex; flex-direction:column; gap:6px;">
                                        <label style="font-size:13px; font-weight:600;">T&#7891;n kho c&#7843;nh b&#225;o</label>
                                        <input type="number" name="stockAlertQty" min="0" value="<c:out value='${product.stockAlertQty}'/>" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; box-sizing:border-box;"/>
                                    </div>
                                </div>
                            </section>

                        </div>
                    </div>

                    <%-- Tab: Lich su hoat dong --%>
                    <div class="kr-tab-panel kr-tab-history" style="padding:24px;">
                        <c:choose>
                            <c:when test="${empty activities}">
                                <div style="padding:24px; border:1px dashed #dfe3e8; border-radius:10px; color:#5f6368; text-align:center; background:#f8fafc;">Ch&#432;a c&#243; l&#7883;ch s&#7917; ho&#7841;t &#273;&#7897;ng</div>
                            </c:when>
                            <c:otherwise>
                                <div style="display:flex; flex-direction:column; gap:12px;">
                                    <c:forEach var="activity" items="${activities}">
                                        <div style="display:flex; align-items:flex-start; justify-content:space-between; gap:16px; padding:14px 16px; border:1px solid #e8eaed; border-radius:10px; background:#fff;">
                                            <div style="display:flex; flex-direction:column; gap:6px;">
                                                <div style="font-size:14px; font-weight:700; color:#15171a;"><c:out value="${activity.description}"/></div>
                                                <div style="font-size:12px; color:#5f6368;">Ng&#432;&#7901;i th&#7921;c hi&#7879;n: <c:out value="${activity.createdBy}" default="H&#7879; th&#7889;ng"/></div>
                                            </div>
                                            <span style="font-size:12px; color:#0070f4; background:#e8f1ff; border-radius:999px; padding:5px 10px; text-transform:uppercase; white-space:nowrap;"><c:out value="${activity.type}"/></span>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>
