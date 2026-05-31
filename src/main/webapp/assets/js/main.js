// KiotRetail Main JavaScript

// Initialize tooltips
document.addEventListener('DOMContentLoaded', function() {
    // Bootstrap tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });
});

// Format currency
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

// Format number
function formatNumber(number) {
    return new Intl.NumberFormat('vi-VN').format(number);
}

// Confirm delete
function confirmDelete(message) {
    return confirm(message || 'Bạn có chắc chắn muốn xóa?');
}

// Show loading spinner
function showLoading() {
    const loadingHtml = `
        <div class="position-fixed top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center"
             style="background: rgba(0,0,0,0.5); z-index: 9999;" id="loadingOverlay">
            <div class="spinner-border text-light" role="status" style="width: 3rem; height: 3rem;">
                <span class="visually-hidden">Loading...</span>
            </div>
        </div>
    `;
    document.body.insertAdjacentHTML('beforeend', loadingHtml);
}

// Hide loading spinner
function hideLoading() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) {
        overlay.remove();
    }
}

// Debounce function for search
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Print function
function printPage() {
    window.print();
}

// Export to CSV
function exportToCSV(data, filename) {
    const csv = data.map(row => row.join(',')).join('\n');
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    window.URL.revokeObjectURL(url);
}

// Toggle sidebar on mobile
function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    if (sidebar) {
        sidebar.classList.toggle('show');
    }
}

// Form validation
function validateForm(formId) {
    const form = document.getElementById(formId);
    if (!form) return false;

    const inputs = form.querySelectorAll('input[required], select[required], textarea[required]');
    let isValid = true;

    inputs.forEach(input => {
        if (!input.value.trim()) {
            input.classList.add('is-invalid');
            isValid = false;
        } else {
            input.classList.remove('is-invalid');
        }
    });

    return isValid;
}

// Clear form
function clearForm(formId) {
    const form = document.getElementById(formId);
    if (form) {
        form.reset();
        form.querySelectorAll('.is-invalid').forEach(el => {
            el.classList.remove('is-invalid');
        });
    }
}

// Show toast notification (react-hot-toast style)
function showToast(message, type, duration) {
    type = type || 'success';
    duration = duration || 4000;

    var container = document.querySelector('.kr-toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'kr-toast-container';
        document.body.appendChild(container);
    }

    var iconHtml = '';
    if (type === 'loading') {
        iconHtml = '<div class="kr-toast-icon-wrap"><div class="kr-toast-loader"></div></div>';
    } else {
        iconHtml = '<div class="kr-toast-icon-wrap"><div class="circle ' + type + '"></div></div>';
    }

    var toast = document.createElement('div');
    toast.className = 'kr-toast';
    toast.innerHTML = iconHtml + '<div class="kr-toast-msg">' + message + '</div>';
    container.appendChild(toast);

    if (type !== 'loading') {
        var timer = setTimeout(function() { removeToast(toast); }, duration);
        toast._timer = timer;
    }

    toast.addEventListener('mouseenter', function() {
        if (toast._timer) clearTimeout(toast._timer);
    });
    toast.addEventListener('mouseleave', function() {
        if (type !== 'loading') {
            toast._timer = setTimeout(function() { removeToast(toast); }, 2000);
        }
    });

    function removeToast(el) {
        if (el.classList.contains('removing')) return;
        el.classList.add('removing');
        el.addEventListener('animationend', function() { el.remove(); });
    }

    return toast;
}

function dismissToast(toast) {
    if (!toast || toast.classList.contains('removing')) return;
    toast.classList.add('removing');
    toast.addEventListener('animationend', function() { toast.remove(); });
}

// Promise-style toast (loading -> success/error)
function toastPromise(promise, msgs) {
    msgs = msgs || {};
    var loading = showToast(msgs.loading || 'Đang xử lý...', 'loading');
    promise.then(function(result) {
        dismissToast(loading);
        var msg = typeof msgs.success === 'function' ? msgs.success(result) : (msgs.success || 'Thành công!');
        showToast(msg, 'success');
    }).catch(function(err) {
        dismissToast(loading);
        var msg = typeof msgs.error === 'function' ? msgs.error(err) : (msgs.error || 'Có lỗi xảy ra!');
        showToast(msg, 'danger');
    });
    return promise;
}

// AJAX helper
function ajax(url, method, data, successCallback, errorCallback) {
    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json',
        },
        body: data ? JSON.stringify(data) : null
    })
    .then(response => response.json())
    .then(data => {
        if (successCallback) successCallback(data);
    })
    .catch(error => {
        console.error('Error:', error);
        if (errorCallback) errorCallback(error);
    });
}

// Date formatting
function formatDate(dateString) {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    }).format(date);
}

// Calculate percentage
function calculatePercentage(value, total) {
    if (total === 0) return 0;
    return ((value / total) * 100).toFixed(2);
}

// Generate random ID
function generateId(prefix = 'id') {
    return prefix + '_' + Math.random().toString(36).substr(2, 9);
}

// Copy to clipboard
function copyToClipboard(text) {
    navigator.clipboard.writeText(text).then(() => {
        showToast('Đã sao chép vào clipboard', 'success');
    }).catch(err => {
        console.error('Failed to copy:', err);
        showToast('Không thể sao chép', 'danger');
    });
}

// Check if mobile device
function isMobile() {
    return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
}

// Smooth scroll to element
function scrollToElement(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
}

// Local storage helpers
const storage = {
    set: (key, value) => {
        try {
            localStorage.setItem(key, JSON.stringify(value));
        } catch (e) {
            console.error('Error saving to localStorage:', e);
        }
    },
    get: (key) => {
        try {
            const item = localStorage.getItem(key);
            return item ? JSON.parse(item) : null;
        } catch (e) {
            console.error('Error reading from localStorage:', e);
            return null;
        }
    },
    remove: (key) => {
        try {
            localStorage.removeItem(key);
        } catch (e) {
            console.error('Error removing from localStorage:', e);
        }
    },
    clear: () => {
        try {
            localStorage.clear();
        } catch (e) {
            console.error('Error clearing localStorage:', e);
        }
    }
};

// Export functions for use in other scripts
window.KiotRetail = {
    formatCurrency,
    formatNumber,
    formatDate,
    confirmDelete,
    showLoading,
    hideLoading,
    showToast,
    dismissToast,
    toastPromise,
    ajax,
    validateForm,
    clearForm,
    toggleSidebar,
    exportToCSV,
    printPage,
    debounce,
    calculatePercentage,
    generateId,
    copyToClipboard,
    isMobile,
    scrollToElement,
    storage
};
