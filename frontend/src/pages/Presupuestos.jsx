import { useEffect, useState } from 'react';
import Layout from '../components/layout/Layout';
import Table from '../components/ui/Table';
import Button from '../components/ui/Button';
import Input from '../components/ui/Input';
import Modal from '../components/ui/Modal';
import Badge from '../components/ui/Badge';

import {
  getPresupuestos,
  createPresupuesto,
  aprobarPresupuesto,
  rechazarPresupuesto,
  deletePresupuesto,
} from '../api/presupuestos';

import { getOrdenes } from '../api/ordenes';

const EMPTY = {
  ordenTrabajoId: '',
  manoObra: '',
  detalle: '',
};

export default function Presupuestos() {
  const [presupuestos, setPresupuestos] = useState([]);
  const [ordenes, setOrdenes] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [feedback, setFeedback] = useState(null);

  const [modal, setModal] = useState(null);
  const [selected, setSelected] = useState(null);

  const [form, setForm] = useState(EMPTY);
  const [formLoading, setFormLoading] = useState(false);
  const [formError, setFormError] = useState('');

  const load = async () => {
    setLoading(true);
    setError('');

    try {
      const [presupuestosData, ordenesData] =
        await Promise.all([
          getPresupuestos(),
          getOrdenes(),
        ]);

      setPresupuestos(
        Array.isArray(presupuestosData)
          ? presupuestosData
          : []
      );

      setOrdenes(
        Array.isArray(ordenesData)
          ? ordenesData
          : []
      );
    } catch (err) {
      setError(
        err.message ||
        'No fue posible cargar los presupuestos.'
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const ordenLabel = (id) => {
    const orden = ordenes.find(
      item =>
        item.id === id ||
        item.id === Number(id)
    );

    return orden
      ? `#${orden.id} — ${
          orden.vehiculo?.patente ?? ''
        }`
      : `Orden #${id}`;
  };

  const esPendiente = presupuesto =>
    presupuesto?.estadoAprobacion ===
    'PENDIENTE';

  const esAceptado = presupuesto =>
    presupuesto?.estadoAprobacion ===
      'ACEPTADO' ||
    presupuesto?.estadoAprobacion ===
      'APROBADO';

  const openCreate = () => {
    setForm(EMPTY);
    setFormError('');
    setSelected(null);
    setModal('create');
  };

  const openDetail = presupuesto => {
    setSelected(presupuesto);
    setFormError('');
    setModal('detail');
  };

  const openDelete = presupuesto => {
    setSelected(presupuesto);
    setFormError('');
    setModal('delete');
  };

  const openAcceptConfirmation = () => {
    setFormError('');
    setModal('accept');
  };

  const openRejectConfirmation = () => {
    setFormError('');
    setModal('reject');
  };

  const close = () => {
    if (formLoading) {
      return;
    }

    setModal(null);
    setSelected(null);
    setFormError('');
  };

  const handleSave = async event => {
    event.preventDefault();

    setFormLoading(true);
    setFormError('');
    setFeedback(null);

    try {
      await createPresupuesto({
        ordenTrabajoId: Number(
          form.ordenTrabajoId
        ),
        manoObra: Number(form.manoObra),
        detalle: form.detalle,
      });

      await load();

      setModal(null);
      setSelected(null);
      setForm(EMPTY);

      setFeedback({
        type: 'success',
        message:
          'Presupuesto creado correctamente.',
      });
    } catch (err) {
      setFormError(
        err.message ||
        'No fue posible crear el presupuesto.'
      );
    } finally {
      setFormLoading(false);
    }
  };

  const handleAceptar = async () => {
    if (!selected?.id) {
      return;
    }

    setFormLoading(true);
    setFormError('');
    setFeedback(null);

    try {
      await aprobarPresupuesto(selected.id);

      await load();

      setModal(null);
      setSelected(null);

      setFeedback({
        type: 'success',
        message:
          'Presupuesto aceptado correctamente.',
      });
    } catch (err) {
      setFormError(
        err.message ||
        'No fue posible aceptar el presupuesto.'
      );
    } finally {
      setFormLoading(false);
    }
  };

  const handleRechazar = async () => {
    if (!selected?.id) {
      return;
    }

    setFormLoading(true);
    setFormError('');
    setFeedback(null);

    try {
      await rechazarPresupuesto(selected.id);

      await load();

      setModal(null);
      setSelected(null);

      setFeedback({
        type: 'success',
        message:
          'Presupuesto rechazado correctamente.',
      });
    } catch (err) {
      setFormError(
        err.message ||
        'No fue posible rechazar el presupuesto.'
      );
    } finally {
      setFormLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!selected?.id) {
      return;
    }

    setFormLoading(true);
    setFormError('');
    setFeedback(null);

    try {
      await deletePresupuesto(selected.id);

      await load();

      setModal(null);
      setSelected(null);

      setFeedback({
        type: 'success',
        message:
          'Presupuesto eliminado correctamente.',
      });
    } catch (err) {
      setFormError(
        err.message ||
        'No fue posible eliminar el presupuesto.'
      );
    } finally {
      setFormLoading(false);
    }
  };

  const estadoBadge = estado => {
    const estados = {
      PENDIENTE: 'PENDIENTE',
      ACEPTADO: 'FINALIZADA',

      // Compatibilidad con registros antiguos.
      APROBADO: 'FINALIZADA',

      RECHAZADO: 'CANCELADA',
    };

    return (
      <Badge
        status={
          estados[estado] || 'PENDIENTE'
        }
      />
    );
  };

  /*
   * No mostramos órdenes que ya tienen un
   * presupuesto registrado.
   */
  const ordenesDisponibles = ordenes.filter(
    orden =>
      !presupuestos.some(
        presupuesto =>
          Number(
            presupuesto.ordenTrabajo?.id
          ) === Number(orden.id)
      )
  );

  const columns = [
    {
      key: 'id',
      header: 'ID',
      render: presupuesto =>
        `#${presupuesto.id}`,
    },
    {
      key: 'orden',
      header: 'Orden',
      render: presupuesto =>
        ordenLabel(
          presupuesto.ordenTrabajo?.id
        ),
    },
    {
      key: 'manoObra',
      header: 'Mano de obra',
      render: presupuesto =>
        `$${Number(
          presupuesto.manoObra ?? 0
        ).toLocaleString('es-CL')}`,
    },
    {
      key: 'montoTotal',
      header: 'Total',
      render: presupuesto => (
        <span className="font-semibold">
          $
          {Number(
            presupuesto.montoTotal ?? 0
          ).toLocaleString('es-CL')}
        </span>
      ),
    },
    {
      key: 'estado',
      header: 'Estado',
      render: presupuesto =>
        estadoBadge(
          presupuesto.estadoAprobacion
        ),
    },
    {
      key: 'acciones',
      header: 'Acciones',
      render: presupuesto => (
        <div className="flex gap-1 flex-wrap">
          <button
            type="button"
            title="Ver presupuesto"
            onClick={() =>
              openDetail(presupuesto)
            }
            className="p-1.5 rounded-lg text-slate-500 hover:bg-blue-50 hover:text-blue-600 transition-colors"
          >
            <svg
              className="h-4 w-4"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
              />

              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
              />
            </svg>
          </button>

          {!esAceptado(presupuesto) && (
            <button
              type="button"
              title="Eliminar presupuesto"
              onClick={() =>
                openDelete(presupuesto)
              }
              className="p-1.5 rounded-lg text-slate-500 hover:bg-red-50 hover:text-red-600 transition-colors"
            >
              <svg
                className="h-4 w-4"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                />
              </svg>
            </button>
          )}
        </div>
      ),
    },
  ];

  return (
    <Layout title="Presupuestos">
      {error && (
        <div className="mb-4 px-4 py-3 rounded-lg bg-red-50 border border-red-200 text-red-700 text-sm">
          {error}
        </div>
      )}

      {feedback && (
        <div
          className={`mb-4 px-4 py-3 rounded-lg border text-sm ${
            feedback.type === 'success'
              ? 'bg-green-50 border-green-200 text-green-700'
              : 'bg-red-50 border-red-200 text-red-700'
          }`}
        >
          {feedback.message}
        </div>
      )}

      <div className="flex justify-end mb-4">
        <Button onClick={openCreate}>
          <svg
            className="h-4 w-4"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M12 4v16m8-8H4"
            />
          </svg>

          Nuevo Presupuesto
        </Button>
      </div>

      <Table
        columns={columns}
        data={presupuestos}
        loading={loading}
        emptyMessage="No hay presupuestos registrados."
      />

      {/* Modal para crear presupuesto */}
      <Modal
        isOpen={modal === 'create'}
        onClose={close}
        title="Nuevo Presupuesto"
        footer={
          <>
            <Button
              variant="secondary"
              onClick={close}
              disabled={formLoading}
            >
              Cancelar
            </Button>

            <Button
              type="submit"
              form="pres-form"
              loading={formLoading}
            >
              Guardar
            </Button>
          </>
        }
      >
        {formError && (
          <p className="mb-3 text-sm text-red-600">
            {formError}
          </p>
        )}

        <form
          id="pres-form"
          onSubmit={handleSave}
          className="space-y-3"
        >
          <div className="flex flex-col gap-1">
            <label className="text-sm font-medium text-slate-700">
              Orden de Trabajo{' '}
              <span className="text-red-500">
                *
              </span>
            </label>

            <select
              value={form.ordenTrabajoId}
              onChange={event =>
                setForm(current => ({
                  ...current,
                  ordenTrabajoId:
                    event.target.value,
                }))
              }
              required
              className="px-3 py-2 rounded-lg border border-slate-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">
                Seleccionar orden…
              </option>

              {ordenesDisponibles.map(
                orden => (
                  <option
                    key={orden.id}
                    value={orden.id}
                  >
                    #{orden.id} —{' '}
                    {orden.vehiculo?.patente ??
                      ''}{' '}
                    ({orden.estado})
                  </option>
                )
              )}
            </select>

            {ordenesDisponibles.length ===
              0 && (
              <p className="text-xs text-amber-600">
                No existen órdenes disponibles
                para generar un nuevo
                presupuesto.
              </p>
            )}
          </div>

          <Input
            label="Mano de obra ($)"
            id="mano"
            type="number"
            min="0"
            step="1"
            value={form.manoObra}
            onChange={event =>
              setForm(current => ({
                ...current,
                manoObra: event.target.value,
              }))
            }
            required
          />

          <div className="flex flex-col gap-1">
            <label
              htmlFor="detalle"
              className="text-sm font-medium text-slate-700"
            >
              Detalle
            </label>

            <textarea
              id="detalle"
              value={form.detalle}
              onChange={event =>
                setForm(current => ({
                  ...current,
                  detalle: event.target.value,
                }))
              }
              rows={3}
              className="px-3 py-2 rounded-lg border border-slate-300 text-sm resize-none focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </form>
      </Modal>

      {/* Modal de detalle */}
      <Modal
        isOpen={modal === 'detail'}
        onClose={close}
        title={`Presupuesto #${selected?.id}`}
        footer={
          <div className="flex gap-2 w-full">
            <Button
              variant="secondary"
              onClick={close}
              className="mr-auto"
            >
              Cerrar
            </Button>

            {esPendiente(selected) && (
              <>
                <Button
                  variant="danger"
                  onClick={
                    openRejectConfirmation
                  }
                >
                  Rechazar
                </Button>

                <Button
                  onClick={
                    openAcceptConfirmation
                  }
                >
                  Aceptar
                </Button>
              </>
            )}
          </div>
        }
      >
        {selected && (
          <dl className="space-y-3 text-sm">
            <div>
              <dt className="font-medium text-slate-500">
                Orden
              </dt>

              <dd>
                {ordenLabel(
                  selected.ordenTrabajo?.id
                )}
              </dd>
            </div>

            <div>
              <dt className="font-medium text-slate-500">
                Estado
              </dt>

              <dd>
                {estadoBadge(
                  selected.estadoAprobacion
                )}
              </dd>
            </div>

            <div>
              <dt className="font-medium text-slate-500">
                Repuestos
              </dt>

              <dd>
                $
                {Number(
                  selected.montoRepuestos ?? 0
                ).toLocaleString('es-CL')}
              </dd>
            </div>

            <div>
              <dt className="font-medium text-slate-500">
                Mano de obra
              </dt>

              <dd>
                $
                {Number(
                  selected.manoObra ?? 0
                ).toLocaleString('es-CL')}
              </dd>
            </div>

            <div>
              <dt className="font-medium text-slate-500">
                Total
              </dt>

              <dd className="font-bold text-lg">
                $
                {Number(
                  selected.montoTotal ?? 0
                ).toLocaleString('es-CL')}
              </dd>
            </div>

            {selected.fechaGeneracion && (
              <div>
                <dt className="font-medium text-slate-500">
                  Fecha de generación
                </dt>

                <dd>
                  {new Date(
                    selected.fechaGeneracion
                  ).toLocaleString('es-CL')}
                </dd>
              </div>
            )}

            {selected.detalle && (
              <div>
                <dt className="font-medium text-slate-500">
                  Detalle
                </dt>

                <dd className="whitespace-pre-wrap">
                  {selected.detalle}
                </dd>
              </div>
            )}

            {!esPendiente(selected) && (
              <div className="px-3 py-2 rounded-lg bg-slate-50 border border-slate-200 text-slate-600">
                Este presupuesto ya fue resuelto
                y no puede aceptarse ni
                rechazarse nuevamente.
              </div>
            )}
          </dl>
        )}
      </Modal>

      {/* Confirmar aceptación */}
      <Modal
        isOpen={modal === 'accept'}
        onClose={close}
        title="Aceptar Presupuesto"
        size="sm"
        footer={
          <>
            <Button
              variant="secondary"
              onClick={() =>
                setModal('detail')
              }
              disabled={formLoading}
            >
              Cancelar
            </Button>

            <Button
              onClick={handleAceptar}
              loading={formLoading}
            >
              Sí, aceptar
            </Button>
          </>
        }
      >
        {formError && (
          <p className="mb-3 text-sm text-red-600">
            {formError}
          </p>
        )}

        <p className="text-slate-600 text-sm">
          ¿Está seguro de que desea aceptar el
          presupuesto{' '}
          <strong>#{selected?.id}</strong>?
        </p>

        <p className="mt-2 text-xs text-amber-600">
          Una vez aceptado, el presupuesto no
          podrá rechazarse ni eliminarse.
        </p>
      </Modal>

      {/* Confirmar rechazo */}
      <Modal
        isOpen={modal === 'reject'}
        onClose={close}
        title="Rechazar Presupuesto"
        size="sm"
        footer={
          <>
            <Button
              variant="secondary"
              onClick={() =>
                setModal('detail')
              }
              disabled={formLoading}
            >
              Cancelar
            </Button>

            <Button
              variant="danger"
              onClick={handleRechazar}
              loading={formLoading}
            >
              Sí, rechazar
            </Button>
          </>
        }
      >
        {formError && (
          <p className="mb-3 text-sm text-red-600">
            {formError}
          </p>
        )}

        <p className="text-slate-600 text-sm">
          ¿Está seguro de que desea rechazar el
          presupuesto{' '}
          <strong>#{selected?.id}</strong>?
        </p>

        <p className="mt-2 text-xs text-amber-600">
          Una vez rechazado, no podrá aceptarse
          posteriormente.
        </p>
      </Modal>

      {/* Eliminar presupuesto */}
      <Modal
        isOpen={modal === 'delete'}
        onClose={close}
        title="Eliminar Presupuesto"
        size="sm"
        footer={
          <>
            <Button
              variant="secondary"
              onClick={close}
              disabled={formLoading}
            >
              Cancelar
            </Button>

            <Button
              variant="danger"
              onClick={handleDelete}
              loading={formLoading}
            >
              Eliminar
            </Button>
          </>
        }
      >
        {formError && (
          <p className="mb-3 text-sm text-red-600">
            {formError}
          </p>
        )}

        <p className="text-slate-600 text-sm">
          ¿Está seguro de que desea eliminar el
          presupuesto{' '}
          <strong>#{selected?.id}</strong>?
        </p>

        <p className="mt-2 text-xs text-amber-600">
          No será posible eliminarlo si fue
          aceptado o si tiene un documento de
          pago asociado.
        </p>
      </Modal>
    </Layout>
  );
}