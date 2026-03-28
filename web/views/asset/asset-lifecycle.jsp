<%-- 
    Document   : asset-lifecycle
    Created on : Mar 27, 2026, 10:53:37 PM
    Author     : An
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.asset.AssetLifecycleEvent" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Vòng đời tài sản | School Asset Management</title>
        <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
        <style>
            .timeline {
                position: relative;
                padding: 0;
                list-style: none;
            }
            .timeline:before {
                content: '';
                position: absolute;
                top: 0;
                bottom: 0;
                left: 32px;
                width: 3px;
                background: #e3e6f0;
            }
            .timeline-item {
                position: relative;
                margin-bottom: 24px;
                padding-left: 70px;
            }
            .timeline-icon {
                position: absolute;
                left: 14px;
                top: 0;
                width: 36px;
                height: 36px;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                color: white;
                font-size: 14px;
            }
            .icon-new       {
                background: #1cc88a;
            }
            .icon-status    {
                background: #f6c23e;
            }
            .icon-allocation{
                background: #36b9cc;
            }
            .icon-transfer  {
                background: #4e73df;
            }
            .icon-deleted   {
                background: #e74a3b;
            }
            .icon-other     {
                background: #858796;
            }
            .timeline-content {
                background: #fff;
                border: 1px solid #e3e6f0;
                border-radius: 8px;
                padding: 12px 16px;
                box-shadow: 0 1px 4px rgba(0,0,0,.06);
            }
            .timeline-time {
                font-size: 12px;
                color: #858796;
            }
            .badge-update {
                background-color: #9b59b6;
                color: white;
            }
            .icon-update {
                background: #9b59b6;
            }

        </style>
    </head>
    <body id="page-top">
        <div id="wrapper">

            <%@ include file="/views/layout/sidebar.jsp" %>

            <div id="content-wrapper" class="d-flex flex-column">
                <div id="content">
                    <%@ include file="/views/layout/topbar.jsp" %>

                    <div class="container-fluid">

                        <!-- Heading -->
                        <div class="d-sm-flex align-items-center justify-content-between mb-4">
                            <h1 class="h3 mb-0 text-gray-800">
                                <i class="fas fa-history text-primary"></i>
                                Vòng đời tài sản:
                                <strong>${asset.assetCode}</strong> — ${asset.assetName}
                            </h1>
                            <a href="${pageContext.request.contextPath}/assets?action=detail&id=${asset.assetId}"
                               class="btn btn-sm btn-secondary shadow-sm">
                                <i class="fas fa-arrow-left"></i> Quay lại chi tiết
                            </a>
                        </div>

                        <!-- Timeline -->
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">
                                    <i class="fas fa-stream"></i> Timeline sự kiện
                                    <span class="badge badge-primary ml-2">${events.size()} sự kiện</span>
                                </h6>
                            </div>
                            <div class="card-body">
                                <c:choose>
                                    <c:when test="${empty events}">
                                        <div class="alert alert-info">
                                            <i class="fas fa-info-circle"></i>
                                            Chưa có sự kiện nào được ghi nhận cho tài sản này.
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <ul class="timeline">
                                            <c:forEach var="ev" items="${events}">
                                                <li class="timeline-item">
                                                    <%-- Icon theo type --%>
                                                    <c:choose>
                                                        <c:when test="${ev.type == 'NEW'}">
                                                            <div class="timeline-icon icon-new">
                                                                <i class="fas fa-plus"></i>
                                                            </div>
                                                        </c:when>
                                                        <c:when test="${ev.type == 'STATUS_CHANGE'}">
                                                            <div class="timeline-icon icon-status">
                                                                <i class="fas fa-exchange-alt"></i>
                                                            </div>
                                                        </c:when>
                                                        <c:when test="${ev.type == 'ALLOCATION'}">
                                                            <div class="timeline-icon icon-allocation">
                                                                <i class="fas fa-hand-holding"></i>
                                                            </div>
                                                        </c:when>
                                                        <c:when test="${ev.type == 'TRANSFER'}">
                                                            <div class="timeline-icon icon-transfer">
                                                                <i class="fas fa-truck"></i>
                                                            </div>
                                                        </c:when>
                                                        <c:when test="${ev.type == 'DELETED'}">
                                                            <div class="timeline-icon icon-deleted">
                                                                <i class="fas fa-trash"></i>
                                                            </div>
                                                        </c:when>
                                                        <c:when test="${ev.type == 'UPDATE_INFO'}">
                                                            <div class="timeline-icon icon-update">
                                                                <i class="fas fa-pen"></i>
                                                            </div>
                                                        </c:when>

                                                        <c:otherwise>
                                                            <div class="timeline-icon icon-other">
                                                                <i class="fas fa-circle"></i>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>

                                                    <div class="timeline-content">
                                                        <div class="d-flex justify-content-between align-items-start">
                                                            <div>
                                                                <span class="badge ${ev.typeBadgeClass} mr-2">
                                                                    ${ev.typeText}
                                                                </span>
                                                                <c:if test="${ev.newStatus != null}">
                                                                    <c:if test="${ev.oldStatus != null}">
                                                                        <small class="text-muted">
                                                                            ${ev.oldStatus} →
                                                                        </small>
                                                                    </c:if>
                                                                    <strong>${ev.newStatus}</strong>
                                                                </c:if>
                                                            </div>
                                                            <span class="timeline-time">
                                                                <i class="fas fa-clock"></i>
                                                                <fmt:formatDate value="${ev.changedAtAsDate}"
                                                                                pattern="dd/MM/yyyy HH:mm:ss"/>
                                                            </span>
                                                        </div>

                                                        <%-- Thông tin phòng (nếu có) --%>
                                                        <c:if test="${ev.oldRoomName != null || ev.newRoomName != null}">
                                                            <div class="mt-1 small">
                                                                <i class="fas fa-door-open text-muted"></i>
                                                                <c:if test="${ev.oldRoomName != null}">
                                                                    Từ: <strong>${ev.oldRoomName}</strong>
                                                                </c:if>
                                                                <c:if test="${ev.newRoomName != null}">
                                                                    → Đến: <strong>${ev.newRoomName}</strong>
                                                                </c:if>
                                                            </div>
                                                        </c:if>

                                                        <%-- Lý do --%>
                                                        <c:if test="${ev.reason != null && !empty ev.reason}">
                                                            <div class="mt-1 small text-muted">
                                                                <i class="fas fa-comment-alt"></i>
                                                                ${ev.reason}
                                                            </div>
                                                        </c:if>

                                                        <%-- Người thực hiện --%>
                                                        <div class="mt-1 small text-muted">
                                                            <i class="fas fa-user"></i>
                                                            Người thực hiện:
                                                            <strong>${ev.changedByName != null ? ev.changedByName : 'Hệ thống'}</strong>
                                                        </div>
                                                    </div>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                    </div>
                </div>
                <%@ include file="/views/layout/footer.jsp" %>
            </div>
        </div>

        <script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/vendor/jquery-easing/jquery.easing.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>
    </body>
</html>
