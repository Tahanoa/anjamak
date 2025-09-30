// متغیرهای سراسری
let currentPage = 0;
let currentSearchTerm = '';
let currentEditingTaskId = null;
const API_BASE_URL = 'http://localhost:8080/tasks'; // آدرس API

// رویدادهای صفحه
document.addEventListener('DOMContentLoaded', function() {
    console.log('صفحه بارگذاری شد');
    loadTasks();

    // رویداد فرم افزودن کار
    document.getElementById('taskForm').addEventListener('submit', function(e) {
        e.preventDefault();
        addTask();
    });

    // رویداد جستجو
    document.getElementById('searchInput').addEventListener('input', function(e) {
        currentSearchTerm = e.target.value;
        currentPage = 0;
        loadTasks();
    });

    // رویداد فرم ویرایش
    document.getElementById('editForm').addEventListener('submit', function(e) {
        e.preventDefault();
        updateTask();
    });
});

// تابع بارگذاری کارها
async function loadTasks() {
    console.log('بارگذاری کارها شروع شد');
    showLoading();

    try {
        let url;
        if (currentSearchTerm.trim()) {
            url = `${API_BASE_URL}/findAllByTitle/${encodeURIComponent(currentSearchTerm)}?page=${currentPage}&size=10`;
        } else {
            url = `${API_BASE_URL}/findAll?page=${currentPage}&size=10`;
        }

        console.log('درخواست به URL:', url);

        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        });

        console.log('وضعیت پاسخ:', response.status);
        console.log('وضعیت OK:', response.ok);

        if (!response.ok) {
            const errorText = await response.text();
            console.error('خطای سرور:', errorText);
            throw new Error(`خطا در بارگذاری کارها: ${response.status}`);
        }

        const data = await response.json();
        console.log('داده دریافتی:', data);

        // بررسی ساختار داده
        if (data && data.content) {
            renderTasks(data.content);
        } else if (Array.isArray(data)) {
            renderTasks(data);
        } else {
            console.warn('ساختار داده غیرمنتظره:', data);
            renderTasks([]);
        }
    } catch (error) {
        console.error('خطا در بارگذاری کارها:', error);
        showAlert('خطا در بارگذاری کارها: ' + error.message, 'error');
        renderTasks([]);
    }
}

function renderTasks(tasks) {
    const container = document.getElementById('tasksContainer');
    console.log('نمایش کارها:', tasks);

    if (!tasks || tasks.length === 0) {
        container.innerHTML = `
                <div class="empty-state">
                    <i class="bi bi-inbox"></i>
                    <p>هیچ کاری یافت نشد</p>
                </div>
            `;
        return;
    }

    container.innerHTML = tasks.map(task => {
        console.log('نمایش کار:', task);
        return `
                <div class="task-item ${task.completed ? 'completed' : ''}" data-task-id="${task.id}">
                    <div class="task-header">
                        <div class="task-title">${escapeHtml(task.title)}</div>
                        <div class="task-actions">
                            <button class="btn-small ${task.completed ? 'btn-incomplete' : 'btn-complete'}"
                                    onclick="toggleTaskCompletion(${task.id}, ${!task.completed})">
                                <i class="bi ${task.completed ? 'bi-arrow-counterclockwise' : 'bi-check'}"></i>
                            </button>
                            <button class="btn-small btn-edit" onclick="openEditModal(${task.id})">
                                <i class="bi bi-pencil"></i>
                            </button>
                            <button class="btn-small btn-delete" onclick="deleteTask(${task.id})">
                                <i class="bi bi-trash"></i>
                            </button>
                        </div>
                    </div>
                    <div class="task-description">${escapeHtml(task.description || 'بدون توضیحات')}</div>
                    <div style="font-size: 0.8rem; color: #888; margin-top: 0.5rem;">
                        ایجاد: ${new Date(task.createdAt).toLocaleString('fa-IR')}
                    </div>
                </div>
            `;
    }).join('');
}

function showLoading() {
    const container = document.getElementById('tasksContainer');
    container.innerHTML = `
            <div class="loading">
                <i class="bi bi-arrow-clockwise"></i>
                در حال بارگذاری...
            </div>
        `;
}

async function addTask() {
    const title = document.getElementById('taskTitle').value.trim();
    const description = document.getElementById('taskDescription').value.trim();
    const submitBtn = document.getElementById('submitBtn');

    if (title.length < 3 || title.length > 60) {
        showAlert('عنوان باید حداقل 3 و حداکثر 60 کاراکتر باشد!', 'error');
        return;
    }

    if (description.length > 500) {
        showAlert('توضیحات حداکثر 500 کاراکتر است!', 'error');
        return;
    }

    submitBtn.disabled = true;
    submitBtn.innerHTML = '<i class="bi bi-arrow-clockwise"></i> در حال افزودن...';

    try {
        const response = await fetch(`${API_BASE_URL}/addTask`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                title: title,
                description: description
            })
        });

        console.log('وضعیت پاسخ افزودن:', response.status);

        if (response.ok) {
            const result = await response.text();
            showAlert(result || 'وظیفه با موفقیت اضافه شد', 'success');
            document.getElementById('taskForm').reset();
            loadTasks();
        } else {
            try {
                const result = await response.json();
                if (Array.isArray(result)) {
                    const errorMessages = result.map(err => err.message).join('<br>');
                    showAlert(errorMessages, 'error');
                } else {
                    showAlert(result || 'خطا در افزودن کار', 'error');
                }
            } catch (jsonError) {
                const errorText = await response.text();
                showAlert(errorText || 'خطا در افزودن کار', 'error');
            }
        }
    } catch (error) {
        console.error('خطا در افزودن کار:', error);
        showAlert('خطا در ارتباط با سرور: ' + error.message, 'error');
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="bi bi-plus"></i> افزودن کار';
    }
}

async function deleteTask(id) {
    if (!confirm('آیا از حذف این کار اطمینان دارید؟')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/delete/${id}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        console.log('وضعیت پاسخ حذف:', response.status);

        if (response.ok) {
            const result = await response.text();
            showAlert(result || 'وظیفه با موفقیت حذف شد', 'success');
            loadTasks();
        } else {
            const result = await response.text();
            showAlert(result || 'خطا در حذف کار', 'error');
        }
    } catch (error) {
        console.error('خطا در حذف کار:', error);
        showAlert('خطا در ارتباط با سرور: ' + error.message, 'error');
    }
}

async function toggleTaskCompletion(id, completed) {
    try {
        const response = await fetch(`${API_BASE_URL}/${id}/${completed}`, {
            method: 'PUT',
            headers: {
                'Accept': 'application/json'
            }
        });

        console.log('وضعیت پاسخ تغییر وضعیت:', response.status);

        if (response.ok) {
            const result = await response.text();
            showAlert(result || 'وضعیت کار با موفقیت تغییر کرد', 'success');
            loadTasks();
        } else {
            const result = await response.text();
            showAlert(result || 'خطا در تغییر وضعیت کار', 'error');
        }
    } catch (error) {
        console.error('خطا در تغییر وضعیت کار:', error);
        showAlert('خطا در ارتباط با سرور: ' + error.message, 'error');
    }
}

async function openEditModal(id) {
    currentEditingTaskId = id;

    try {
        const response = await fetch(`${API_BASE_URL}/getTaskById/${id}`, {
            headers: {
                'Accept': 'application/json'
            }
        });

        console.log('وضعیت پاسخ دریافت کار:', response.status);

        if (!response.ok) {
            throw new Error('خطا در بارگذاری اطلاعات کار');
        }

        const task = await response.json();
        console.log('کار برای ویرایش:', task);

        document.getElementById('editTitle').value = task.title;
        document.getElementById('editDescription').value = task.description || '';

        document.getElementById('editModal').style.display = 'flex';
    } catch (error) {
        console.error('خطا در بارگذاری اطلاعات کار:', error);
        showAlert('خطا در بارگذاری اطلاعات کار: ' + error.message, 'error');
    }
}

function closeEditModal() {
    document.getElementById('editModal').style.display = 'none';
    currentEditingTaskId = null;
    document.getElementById('editForm').reset();
}

async function updateTask() {
    const title = document.getElementById('editTitle').value.trim();
    const description = document.getElementById('editDescription').value.trim();

    if (title.length < 3 || title.length > 60) {
        showAlert('عنوان باید حداقل 3 و حداکثر 60 کاراکتر باشد!', 'error');
        return;
    }

    if (description.length > 500) {
        showAlert('توضیحات حداکثر 500 کاراکتر است!', 'error');
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/update/${currentEditingTaskId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                title: title,
                description: description
            })
        });

        console.log('وضعیت پاسخ ویرایش:', response.status);

        if (response.ok) {
            const result = await response.text();
            showAlert(result || 'وظیفه با موفقیت ویرایش شد', 'success');
            closeEditModal();
            loadTasks();
        } else {
            const result = await response.text();
            showAlert(result || 'خطا در ویرایش کار', 'error');
        }
    } catch (error) {
        console.error('خطا در ویرایش کار:', error);
        showAlert('خطا در ارتباط با سرور: ' + error.message, 'error');
    }
}

function showAlert(message, type) {
    const alertContainer = document.getElementById('alertContainer');
    const alertClass = type === 'success' ? 'alert-success' : 'alert-error';

    const alertDiv = document.createElement('div');
    alertDiv.className = `alert ${alertClass}`;
    alertDiv.innerHTML = message;

    alertContainer.appendChild(alertDiv);

    setTimeout(() => {
        if (alertDiv.parentNode) {
            alertDiv.parentNode.removeChild(alertDiv);
        }
    }, 5000);
}
function escapeHtml(unsafe) {
    if (!unsafe) return '';
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

function testAPI() {
    console.log('تست API...');
    loadTasks();
}

setTimeout(testAPI, 1000);