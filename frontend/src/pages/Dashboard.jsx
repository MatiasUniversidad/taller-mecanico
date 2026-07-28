import { useEffect, useState } from 'react';
import Layout from '../components/layout/Layout';
import Card from '../components/ui/Card';
import Badge from '../components/ui/Badge';
import Table from '../components/ui/Table';

import { getClientes } from '../api/clientes';
import { getVehiculos } from '../api/vehiculos';
import { getOrdenes } from '../api/ordenes';
import { getReservas } from '../api/reservas';
import { getMecanicos } from '../api/mecanicos';

const ESTADOS_ORDEN = [
  'CREADA',
  'EN_PROCESO',
  'FINALIZADA',
];

export default function Dashboard() {
  const [data, setData] = useState({
    clientes: [],
    vehiculos: [],
    ordenes: [],
    reservas: [],
    mecanicos: [],
  });

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const cargarDatos = async () => {
    setLoading(true);
    setError('');

    try {
      const [
        clientes,
        vehiculos,
        ordenes,
        reservas,
        mecanicos,
      ] = await Promise.all([
        getClientes(),
        getVehiculos(),
        getOrdenes(),
        getReservas(),
        getMecanicos(),
      ]);

      setData({
        clientes: Array.isArray(clientes)
          ? clientes
          : [],

        vehiculos: Array.isArray(vehiculos)
          ? vehiculos
          : [],

        ordenes: Array.isArray(ordenes)
          ? ordenes
          : [],

        reservas: Array.isArray(reservas)
          ? reservas
          : [],

        mecanicos: Array.isArray(mecanicos)
          ? mecanicos
          : [],
      });
    } catch (err) {
      setError(
        err.message ||
          'No fue posible cargar la información del Dashboard.'
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargarDatos();
  }, []);

  const obtenerFechaActual = () => {
    const fecha = new Date();

    const anio = fecha.getFullYear();

    const mes = String(
      fecha.getMonth() + 1
    ).padStart(2, '0');

    const dia = String(
      fecha.getDate()
    ).padStart(2, '0');

    return `${anio}-${mes}-${dia}`;
  };

  const fechaActual = obtenerFechaActual();

  const proximasReservas = [
    ...data.reservas,
  ]
    .filter(
      reserva =>
        reserva.fecha &&
        reserva.fecha >= fechaActual &&
        reserva.estado !== 'CANCELADA'
    )
    .sort((a, b) => {
      const fechaHoraA =
        `${a.fecha ?? ''}${a.hora ?? ''}`;

      const fechaHoraB =
        `${b.fecha ?? ''}${b.hora ?? ''}`;

      return fechaHoraA.localeCompare(
        fechaHoraB
      );
    })
    .slice(0, 5);

  const ordenesActivas =
    data.ordenes.filter(
      orden =>
        orden.estado !== 'FINALIZADA' &&
        orden.estado !== 'CANCELADA'
    ).length;

  const mecanicosDisponibles =
    data.mecanicos.filter(
      mecanico =>
        mecanico.disponible === true
    );

  const mecanicosNoDisponibles =
    data.mecanicos.filter(
      mecanico =>
        mecanico.disponible !== true
    );

  const disponibilidadMecanicos = [
    ...data.mecanicos,
  ].sort((a, b) => {
    if (
      a.disponible === b.disponible
    ) {
      return String(a.nombre ?? '').localeCompare(
        String(b.nombre ?? ''),
        'es'
      );
    }

    return a.disponible ? -1 : 1;
  });

  const reservaColumns = [
    {
      key: 'fecha',
      header: 'Fecha',
    },
    {
      key: 'hora',
      header: 'Hora',
      render: reserva =>
        reserva.hora?.slice(0, 5) ?? '—',
    },
    {
      key: 'cliente',
      header: 'Cliente',
      render: reserva =>
        reserva.cliente?.nombre ?? '—',
    },
    {
      key: 'vehiculo',
      header: 'Vehículo',
      render: reserva =>
        reserva.vehiculo
          ? `${reserva.vehiculo.patente} – ${
              reserva.vehiculo.marca ?? ''
            }`
          : '—',
    },
    {
      key: 'motivo',
      header: 'Motivo',
      render: reserva =>
        reserva.motivo || '—',
    },
    {
      key: 'estado',
      header: 'Estado',
      render: reserva => (
        <Badge status={reserva.estado} />
      ),
    },
  ];

  const mecanicoColumns = [
    {
      key: 'nombre',
      header: 'Mecánico',
      render: mecanico => (
        <div>
          <p className="font-medium text-slate-800">
            {mecanico.nombre}
          </p>

          <p className="text-xs text-slate-500">
            {mecanico.rut}
          </p>
        </div>
      ),
    },
    {
      key: 'especialidad',
      header: 'Especialidad',
      render: mecanico =>
        mecanico.especialidad || 'Sin especialidad',
    },
    {
      key: 'disponibilidad',
      header: 'Disponibilidad',
      render: mecanico =>
        mecanico.disponible ? (
          <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-green-100 text-green-700 text-xs font-semibold">
            <span className="h-2 w-2 rounded-full bg-green-500" />

            Disponible
          </span>
        ) : (
          <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-red-100 text-red-700 text-xs font-semibold">
            <span className="h-2 w-2 rounded-full bg-red-500" />

            No disponible
          </span>
        ),
    },
  ];

  return (
    <Layout title="Dashboard">
      {error && (
        <div className="mb-4 px-4 py-3 rounded-lg bg-red-50 border border-red-200 text-red-700 text-sm">
          {error}
        </div>
      )}

      {/* Tarjetas principales */}
      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4 mb-6">
        <Card
          title="Total Clientes"
          value={
            loading
              ? '…'
              : data.clientes.length
          }
          color="blue"
          icon={
            <svg
              className="h-6 w-6"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z"
              />
            </svg>
          }
        />

        <Card
          title="Total Vehículos"
          value={
            loading
              ? '…'
              : data.vehiculos.length
          }
          color="purple"
          icon={
            <svg
              className="h-6 w-6"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M9 17a2 2 0 11-4 0 2 2 0 014 0zM19 17a2 2 0 11-4 0 2 2 0 014 0z"
              />

              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M13 16V6a1 1 0 00-1-1H4a1 1 0 00-1 1v10l2 1h8zM13 16h2l4-4-1-5h-5v9z"
              />
            </svg>
          }
        />

        <Card
          title="Órdenes Activas"
          value={
            loading
              ? '…'
              : ordenesActivas
          }
          color="yellow"
          icon={
            <svg
              className="h-6 w-6"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"
              />
            </svg>
          }
        />

        <Card
          title="Próximas Reservas"
          value={
            loading
              ? '…'
              : proximasReservas.length
          }
          color="green"
          icon={
            <svg
              className="h-6 w-6"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
              />
            </svg>
          }
        />
      </div>

      {/* Resumen de mecánicos */}
      <div className="mb-6">
        <div className="mb-3">
          <h2 className="text-base font-semibold text-slate-800">
            Disponibilidad de Mecánicos
          </h2>

          <p className="text-sm text-slate-500">
            Resumen actualizado del personal del
            taller.
          </p>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-slate-500">
                  Total Mecánicos
                </p>

                <p className="mt-2 text-3xl font-bold text-slate-800">
                  {loading
                    ? '…'
                    : data.mecanicos.length}
                </p>
              </div>

              <div className="h-11 w-11 rounded-xl bg-blue-100 text-blue-600 flex items-center justify-center">
                <svg
                  className="h-6 w-6"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M17 20h5v-2a4 4 0 00-4-4h-1M9 20H3v-2a4 4 0 014-4h2m6-4a4 4 0 10-8 0 4 4 0 008 0z"
                  />
                </svg>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-xl border border-green-200 p-5 shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-green-700">
                  Disponibles
                </p>

                <p className="mt-2 text-3xl font-bold text-green-700">
                  {loading
                    ? '…'
                    : mecanicosDisponibles.length}
                </p>
              </div>

              <div className="h-11 w-11 rounded-xl bg-green-100 text-green-600 flex items-center justify-center">
                <svg
                  className="h-6 w-6"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M5 13l4 4L19 7"
                  />
                </svg>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-xl border border-red-200 p-5 shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-red-700">
                  No Disponibles
                </p>

                <p className="mt-2 text-3xl font-bold text-red-700">
                  {loading
                    ? '…'
                    : mecanicosNoDisponibles.length}
                </p>
              </div>

              <div className="h-11 w-11 rounded-xl bg-red-100 text-red-600 flex items-center justify-center">
                <svg
                  className="h-6 w-6"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M6 18L18 6M6 6l12 12"
                  />
                </svg>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Órdenes y reservas */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6">
        <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
          <h3 className="text-sm font-semibold text-slate-700 mb-4">
            Órdenes por Estado
          </h3>

          {loading ? (
            <p className="text-slate-400 text-sm">
              Cargando…
            </p>
          ) : (
            <div className="space-y-3">
              {ESTADOS_ORDEN.map(estado => {
                const cantidad =
                  data.ordenes.filter(
                    orden =>
                      orden.estado === estado
                  ).length;

                const total =
                  data.ordenes.length || 1;

                return (
                  <div key={estado}>
                    <div className="flex items-center justify-between mb-1">
                      <Badge status={estado} />

                      <span className="text-sm font-semibold text-slate-700">
                        {cantidad}
                      </span>
                    </div>

                    <div className="h-1.5 bg-slate-100 rounded-full overflow-hidden">
                      <div
                        className="h-full bg-blue-500 rounded-full transition-all"
                        style={{
                          width: `${
                            (cantidad / total) * 100
                          }%`,
                        }}
                      />
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>

        <div className="lg:col-span-2">
          <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
            <h3 className="text-sm font-semibold text-slate-700 mb-4">
              Próximas Reservas
            </h3>

            <Table
              columns={reservaColumns}
              data={proximasReservas}
              loading={loading}
              emptyMessage="No hay reservas próximas."
            />
          </div>
        </div>
      </div>

      {/* Tabla de mecánicos */}
      <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2 mb-4">
          <div>
            <h3 className="text-sm font-semibold text-slate-700">
              Estado de los Mecánicos
            </h3>

            <p className="text-xs text-slate-500 mt-1">
              Los mecánicos disponibles se
              muestran primero.
            </p>
          </div>

          {!loading && (
            <span className="text-xs text-slate-500">
              {mecanicosDisponibles.length} de{' '}
              {data.mecanicos.length} disponibles
            </span>
          )}
        </div>

        <Table
          columns={mecanicoColumns}
          data={disponibilidadMecanicos}
          loading={loading}
          emptyMessage="No hay mecánicos registrados."
        />
      </div>
    </Layout>
  );
}