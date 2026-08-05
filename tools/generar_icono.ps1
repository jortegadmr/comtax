Add-Type -AssemblyName System.Drawing

$root = "C:\Users\jorte\Proton Drive\jortegadmr\My files\Personal_Jose\09_Proyectos_Personales\Programacion\comtax"
$srcPath = Join-Path $root "tools\logo_original.png"
$resPath = Join-Path $root "app\src\main\res"

$src = [System.Drawing.Bitmap]::FromFile($srcPath)
$w = $src.Width
$h = $src.Height

# Chroma-key: los pixeles casi negros de las esquinas (fuera del cuadrado
# redondeado del logo) vienen de un canal alpha aplanado a negro. Los
# convertimos a transparentes para poder encajar el logo en el "safe zone"
# del adaptive icon sin bordes negros.
$transparent = New-Object System.Drawing.Bitmap($w, $h, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
for ($y = 0; $y -lt $h; $y++) {
    for ($x = 0; $x -lt $w; $x++) {
        $p = $src.GetPixel($x, $y)
        if ($p.R -lt 24 -and $p.G -lt 24 -and $p.B -lt 24) {
            $transparent.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(0, 0, 0, 0))
        } else {
            $transparent.SetPixel($x, $y, $p)
        }
    }
}
$transparent.Save((Join-Path $root "tools\logo_transparent.png"), [System.Drawing.Imaging.ImageFormat]::Png)
Write-Output "Chroma-key aplicado."

$densidades = @{
    "mipmap-mdpi"    = 108
    "mipmap-hdpi"    = 162
    "mipmap-xhdpi"   = 216
    "mipmap-xxhdpi"  = 324
    "mipmap-xxxhdpi" = 432
}

foreach ($carpeta in $densidades.Keys) {
    $tam = $densidades[$carpeta]
    $interior = [int]($tam * 0.66)
    $offset = [int](($tam - $interior) / 2)

    $canvas = New-Object System.Drawing.Bitmap($tam, $tam, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($canvas)
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $g.DrawImage($transparent, $offset, $offset, $interior, $interior)
    $g.Dispose()

    $destDir = Join-Path $resPath $carpeta
    New-Item -ItemType Directory -Force -Path $destDir | Out-Null
    $destFile = Join-Path $destDir "ic_launcher_foreground.png"
    $canvas.Save($destFile, [System.Drawing.Imaging.ImageFormat]::Png)
    $canvas.Dispose()
    Write-Output "Generado $destFile ($tam x $tam, logo $interior x $interior)"
}

$transparent.Dispose()
$src.Dispose()
