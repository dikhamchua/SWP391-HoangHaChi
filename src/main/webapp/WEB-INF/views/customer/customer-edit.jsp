<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="activeTab" value="customers" scope="request" />
<fmt:formatDate value="${customer.dateOfBirth}" pattern="yyyy-MM-dd" var="dateOfBirthText" />
<fmt:formatDate value="${customer.createdAt}" pattern="dd/MM/yyyy HH:mm" var="createdAtText" />

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/navbar.jsp" />

<div class="kr-page">
    <div class="kr-main">
        <aside class="kr-sidebar">
            <div style="padding:12px 16px; font-size:13px; font-weight:700; border-bottom:1px solid #e8eaed; background:#f8fafc;">QU&#7842;N L&#221; KH&#193;CH H&#192;NG</div>
            <a href="${ctx}/admin/customers" style="display:flex; align-items:center; gap:10px; padding:10px 16px; font-size:13px; color:#15171a; border-bottom:1px solid #f5f5f5; text-decoration:none;">
                <svg style="width:16px;height:16px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                Danh s&#225;ch kh&#225;ch h&#224;ng
            </a>
            <a href="${ctx}/admin/customers?action=create" style="display:flex; align-items:center; gap:10px; padding:10px 16px; font-size:13px; color:#15171a; border-bottom:1px solid #f5f5f5; text-decoration:none;">
                <svg style="width:16px;height:16px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                Th&#234;m m&#7899;i kh&#225;ch h&#224;ng
            </a>
        </aside>

        <div class="kr-content" style="border:none; background:transparent; overflow:visible;">
            <jsp:include page="../common/toast.jsp"/>
            <c:if test="${not empty errorMessage}">
                <script>document.addEventListener('DOMContentLoaded', function() { showToast('<c:out value="${errorMessage}" escapeXml="true"/>', 'danger'); });</script>
            </c:if>


            <style>
                .kr-tab-radio { display:none; }
                .kr-tab-panel { display:none; }
                #customer-tab-general:checked ~ .kr-tab-card .kr-tab-general,
                #customer-tab-history:checked ~ .kr-tab-card .kr-tab-history { display:block; }
                #customer-tab-general:checked ~ .kr-tab-card label[for="customer-tab-general"],
                #customer-tab-history:checked ~ .kr-tab-card label[for="customer-tab-history"] {
                    color:#0070f4; background:#fff; border-bottom-color:#fff;
                }
            </style>

            <form method="post" action="${ctx}/admin/customers" autocomplete="off">
                <input type="hidden" name="action" value="update" />
                <input type="hidden" name="customerId" value="${customer.customerId}" />

                <div style="display:flex; align-items:flex-start; justify-content:space-between; gap:16px; margin-bottom:16px; flex-wrap:wrap;">
                    <div>
                        <div style="font-size:13px; color:#5f6368; display:flex; align-items:center; gap:6px; margin-bottom:8px;">
                            <a href="${ctx}/admin/customers" style="color:#0070f4; text-decoration:none;">Kh&#225;ch h&#224;ng</a>
                            <span style="color:#9aa0a6;">/</span>
                            <span>Chi ti&#7871;t kh&#225;ch h&#224;ng</span>
                        </div>
                        <h1 class="kr-page-title" style="margin:0;">Chi ti&#7871;t kh&#225;ch h&#224;ng</h1>
                    </div>
                    <div style="display:flex; align-items:center; gap:8px; flex-shrink:0;">
                        <button type="submit" class="kr-btn kr-btn-primary">L&#432;u</button>
                        <a class="kr-btn" href="${ctx}/admin/customers">Quay l&#7841;i</a>
                    </div>
                </div>

                <input class="kr-tab-radio" type="radio" id="customer-tab-general" name="customerTab" checked />
                <input class="kr-tab-radio" type="radio" id="customer-tab-history" name="customerTab" />

                <div class="kr-tab-card" style="background:#fff; border:1px solid #e8eaed; border-radius:10px; overflow:hidden;">
                    <div style="display:flex; align-items:flex-end; gap:8px; padding:0 24px; border-bottom:1px solid #e8eaed; background:#f8fafc;">
                        <label for="customer-tab-general" style="font-size:15px; font-weight:700; color:#5f6368; padding:14px 18px; margin-top:12px; margin-bottom:-1px; background:#f8fafc; border:1px solid #e8eaed; border-radius:8px 8px 0 0; cursor:pointer;">Th&#244;ng tin chung</label>
                        <label for="customer-tab-history" style="font-size:15px; font-weight:700; color:#5f6368; padding:14px 18px; margin-top:12px; margin-bottom:-1px; background:#f8fafc; border:1px solid #e8eaed; border-radius:8px 8px 0 0; cursor:pointer;">L&#7883;ch s&#7917; ho&#7841;t &#273;&#7897;ng</label>
                    </div>
                    <div class="kr-tab-panel kr-tab-general" style="padding:24px;">
                        <div style="display:flex; flex-direction:column; gap:24px;">
                        <section style="display:flex; flex-direction:column; align-items:center; justify-content:center; gap:10px; padding:24px; border:1px solid #eef0f2; border-radius:10px; background:#f8fafc; text-align:center;">
                            <div style="width:88px; height:88px; border-radius:50%; background:#e8f1ff; color:#0070f4; display:flex; align-items:center; justify-content:center; border:3px solid #fff; box-shadow:0 2px 8px rgba(0,0,0,.08);">
                                <svg style="width:42px;height:42px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                            </div>
                            <div style="font-size:20px; font-weight:700; color:#15171a;"><c:out value="${customer.fullName}" /></div>
                            <div style="display:flex; align-items:center; justify-content:center; gap:8px; flex-wrap:wrap;">
                                <span style="font-size:13px; color:#5f6368; background:#fff; border:1px solid #e8eaed; border-radius:999px; padding:5px 12px;">KH${customer.customerId}</span>
                                <span style="font-size:13px; color:#5f6368; background:#fff; border:1px solid #e8eaed; border-radius:999px; padding:5px 12px;"><c:out value="${customer.membershipTier}" /></span>
                                <span style="font-size:13px; color:#5f6368; background:#fff; border:1px solid #e8eaed; border-radius:999px; padding:5px 12px;">${customer.points} &#273;i&#7875;m</span>
                            </div>
                        </section>

                        <section style="display:grid; grid-template-columns:repeat(auto-fit, minmax(260px, 1fr)); gap:20px 16px;">
                            <div style="display:flex; flex-direction:column; gap:8px;">
                                <label style="font-size:13px; font-weight:600;">M&#227; kh&#225;ch h&#224;ng</label>
                                <input type="text" value="KH${customer.customerId}" readonly style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; background:#f8fafc; box-sizing:border-box;"/>
                            </div>
                            <div style="display:flex; flex-direction:column; gap:8px;">
                                <label style="font-size:13px; font-weight:600;">H&#7885; t&#234;n <span style="color:#ef4444;">*</span></label>
                                <input type="text" name="fullName" required maxlength="100" value="<c:out value='${customer.fullName}'/>" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; box-sizing:border-box;"/>
                            </div>
                            <div style="display:flex; flex-direction:column; gap:8px;">
                                <label style="font-size:13px; font-weight:600;">S&#7889; &#273;i&#7879;n tho&#7841;i <span style="color:#ef4444;">*</span></label>
                                <input type="text" name="phone" required maxlength="20" value="<c:out value='${customer.phone}'/>" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; box-sizing:border-box;"/>
                            </div>
                            <div style="display:flex; flex-direction:column; gap:8px;">
                                <label style="font-size:13px; font-weight:600;">Email</label>
                                <input type="email" name="email" maxlength="100" value="<c:out value='${customer.email}'/>" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; box-sizing:border-box;"/>
                            </div>
                            <div style="display:flex; flex-direction:column; gap:8px;">
                                <label style="font-size:13px; font-weight:600;">Gi&#7899;i t&#237;nh</label>
                                <select name="gender" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; background:#fff; box-sizing:border-box;">
                                    <option value="">-- Ch&#7885;n --</option>
                                    <option value="Male" <c:if test="${customer.gender == 'Male'}">selected</c:if>>Nam</option>
                                    <option value="Female" <c:if test="${customer.gender == 'Female'}">selected</c:if>>N&#7919;</option>
                                </select>
                            </div>
                            <div style="display:flex; flex-direction:column; gap:8px;">
                                <label style="font-size:13px; font-weight:600;">Ng&#224;y sinh</label>
                                <input type="date" name="dateOfBirth" value="<c:out value='${dateOfBirthText}'/>" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; box-sizing:border-box;"/>
                            </div>
                            <div style="display:flex; flex-direction:column; gap:8px;">
                                <label style="font-size:13px; font-weight:600;">H&#7841;ng th&#224;nh vi&#234;n</label>
                                <select name="membershipTier" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; background:#fff; box-sizing:border-box;">
                                    <option value="member" <c:if test="${customer.membershipTier == 'member'}">selected</c:if>>Member</option>
                                    <option value="silver" <c:if test="${customer.membershipTier == 'silver'}">selected</c:if>>Silver</option>
                                    <option value="gold" <c:if test="${customer.membershipTier == 'gold'}">selected</c:if>>Gold</option>
                                    <option value="platinum" <c:if test="${customer.membershipTier == 'platinum'}">selected</c:if>>Platinum</option>
                                    <option value="diamond" <c:if test="${customer.membershipTier == 'diamond'}">selected</c:if>>Diamond</option>
                                </select>
                            </div>
                            <div style="display:flex; flex-direction:column; gap:8px;">
                                <label style="font-size:13px; font-weight:600;">&#272;i&#7875;m t&#237;ch l&#361;y</label>
                                <input type="text" value="<c:out value='${customer.points}'/>" readonly style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; background:#f8fafc; box-sizing:border-box;"/>
                            </div>
                            <div style="display:flex; flex-direction:column; gap:8px;">
                                <label style="font-size:13px; font-weight:600;">Ng&#224;y t&#7841;o</label>
                                <input type="text" value="<c:out value='${createdAtText}'/>" readonly style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; background:#f8fafc; box-sizing:border-box;"/>
                            </div>
                            <div style="display:flex; flex-direction:column; gap:8px; grid-column:1/-1;">
                                <label style="font-size:13px; font-weight:600;">&#272;&#7883;a ch&#7881;</label>
                                <input type="text" name="address" maxlength="255" value="<c:out value='${customer.address}'/>" style="width:100%; height:38px; padding:0 12px; border:1px solid #e8eaed; border-radius:6px; font-size:14px; box-sizing:border-box;"/>
                            </div>
                        </section>
                        </div>
                    </div>
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
                                                <div style="font-size:14px; font-weight:700; color:#15171a;"><c:out value="${activity.description}" /></div>
                                                <div style="font-size:12px; color:#5f6368;">Created by: <c:out value="${activity.createdBy}" /></div>
                                            </div>
                                            <span style="font-size:12px; color:#0070f4; background:#e8f1ff; border-radius:999px; padding:5px 10px; text-transform:uppercase;"><c:out value="${activity.type}" /></span>
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

<jsp:include page="../common/footer.jsp" />
