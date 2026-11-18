# Windows / PowerShell 生产环境变量示例（复制后按实际修改，勿提交）
$env:SPRING_PROFILES_ACTIVE = 'druid,prod'

$env:SITE_DB_HOST = '127.0.0.1'
$env:SITE_DB_PORT = '3306'
$env:SITE_DB_NAME = 'site'
$env:SITE_DB_USERNAME = 'site'
$env:SITE_DB_PASSWORD = 'CHANGE_ME'

$env:SITE_TOKEN_SECRET = 'CHANGE_ME_TO_A_64_CHARS_RANDOM_SECRET'

$env:SITE_UPLOAD_PATH = 'D:\site\data\uploadPath'
$env:SITE_LOG_PATH = 'D:\site\data\logs'
$env:SITE_SERVER_PORT = '8080'

$env:SITE_REDIS_HOST = '127.0.0.1'
$env:SITE_REDIS_PORT = '6379'
$env:SITE_REDIS_PASSWORD = ''

$env:SITE_ALLOWED_DOMAINS = 'www.example.com,example.com'
$env:SITE_CORS_ORIGINS = 'https://www.example.com,https://example.com'
